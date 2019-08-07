package com.TyxApp.bangumi.categoryresult;

import android.os.Bundle;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.categoryresult.adapter.CategoryResultAdapter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryResultFragment extends RecyclerViewFragment implements CategoryResultContract.View {
    private CategoryResultContract.Presenter mPresenter;
    private CategoryResultAdapter mAdapter;
    private boolean isLoading;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getRefreshLayout().setEnabled(false);

        mAdapter = new CategoryResultAdapter(requireContext());
        getRecyclerview().setLayoutManager(new GridLayoutManager(requireContext(), 3));
        getRecyclerview().setAdapter(mAdapter);
        getRecyclerview().addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    @Override
    public BasePresenter getPresenter() {
        String currentHomeSoure = PreferenceUtil.getString(getString(R.string.key_home_sourch), BangumiPresistenceContract.BangumiSource.ZZZFUN);
        BaseBangumiParser parser = null;
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

    public static CategoryResultFragment newInstance() {
        return new CategoryResultFragment();
    }

    @Override
    public void showResult(List<Bangumi> results) {
        showRecyclerView();
        mAdapter.addAllInserted(results);
    }

    @Override
    public void showNextResult(List<Bangumi> results) {
        isLoading = false;
        mAdapter.addAllInserted(results);
    }

    @Override
    public void showResultError(Throwable throwable) {
        Snackbar.make(getRecyclerview(), throwable.toString(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResultEmpty() {
        //空结果代表没有更多结果
        getRecyclerview().clearOnScrollListeners();
    }

    @Override
    public void FristLoading() {
        String categoryword = requireActivity().getIntent().getStringExtra(CategoryResultActivity.CATEGORYWORD_KEY);
        mPresenter.getResult(categoryword);
        showDataLodaing();
    }
}
