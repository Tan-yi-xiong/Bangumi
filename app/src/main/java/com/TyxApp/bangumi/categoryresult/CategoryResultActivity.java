package com.TyxApp.bangumi.categoryresult;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.categoryresult.adapter.CategoryResultAdapter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.IBangumiParser;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.player.adapter.TranslationAnimation;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.TyxApp.bangumi.util.TransitionUtils;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;

public class CategoryResultActivity extends BaseMvpActivity implements CategoryResultContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private CategoryResultAdapter mAdapter;
    private CategoryResultContract.Presenter mPresenter;
    private boolean isLoading;
    public static final String CATEGORYWORD_KEY = "C_W_K";

    @Override
    public CategoryResultContract.Presenter getPresenter() {
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        getWindow().setEnterTransition(fade);
        String currentHomeSoure = PreferenceUtil.getString(getString(R.string.key_home_sourch), BangumiPresistenceContract.BangumiSource.ZZZFUN);
        IBangumiParser parser = null;
        switch (currentHomeSoure) {
            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.DILIDLI:
                parser = Dilidili.getInstance();
                break;
        }
        mPresenter = new CategoryResultPresenter(parser, this);
        return mPresenter;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new CategoryResultAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        TranslationAnimation animation = new TranslationAnimation();
        animation.setStartTranslationY(1000);
        animation.setAddDuration(250);
        mRecyclerView.setItemAnimator(animation);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading) {
                        return;
                    }
                    isLoading = true;
                    mPresenter.getNextResult();
                }
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(CATEGORYWORD_KEY));
        toolbar.setNavigationOnClickListener(v -> finish());
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                view.setTransitionName(getString(R.string.search_transition_name));
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_categoryresult;
    }

    public static void startCategoryResultActivity(Context context, String categoryWord) {
        Intent intent = new Intent(context, CategoryResultActivity.class);
        intent.putExtra(CATEGORYWORD_KEY, categoryWord);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter.getDataList().isEmpty()) {
            mPresenter.getResult(getIntent().getStringExtra(CATEGORYWORD_KEY));
        }
    }

    @Override
    public void showResult(List<Bangumi> results) {
        mAdapter.clearAddAll(results);
        isLoading = false;
    }

    @Override
    public void showNextResult(List<Bangumi> results) {
        mAdapter.addAllInserted(results);
        isLoading = false;
    }

    @Override
    public void showResultError(Throwable throwable) {
        Snackbar.make(mRecyclerView, "解析出错", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResultEmpty() {
        //空结果代表没有更多结果
        mRecyclerView.clearOnScrollListeners();
    }
}
