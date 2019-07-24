package com.TyxApp.bangumi.category;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.category.adapter.CategoryAdapter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryFragment extends RecyclerViewFragment implements CategoryContract.View {
    private CategoryContract.Presenter mPresenter;
    private CategoryAdapter mAdapter;
    private boolean isLoading;

    @Override
    protected void initView() {
        getRefreshLayout().setEnabled(false);

        mAdapter = new CategoryAdapter(requireContext());
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
        String currentHomeSoure = PreferenceUtil.getString(PreferenceUtil.HOME_SOURCE, BangumiPresistenceContract.BangumiSource.ZZZFUN);
        BaseBangumiParser parser = null;
        switch (currentHomeSoure) {
            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;
        }
        mPresenter = new CategoryPresenter(parser, this);
        return mPresenter;
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void showResult(List<Bangumi> results) {
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
        String categoryword = requireActivity().getIntent().getStringExtra(CategoryActivity.CATEGORYWORD_KEY);
        mPresenter.getResult(categoryword);
    }
}
