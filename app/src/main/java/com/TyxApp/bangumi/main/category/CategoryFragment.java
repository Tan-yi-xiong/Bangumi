package com.TyxApp.bangumi.main.category;

import android.os.Bundle;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.IBangumiParser;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.main.category.adapter.CategoryAdapter;
import com.TyxApp.bangumi.util.PreferenceUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class CategoryFragment extends RecyclerViewFragment implements CategoryContract.View {
    private String currentHomeSource;
    private CategoryContract.Presenter mPresenter;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getRefreshLayout().setEnabled(false);
        mCategoryAdapter = new CategoryAdapter(requireActivity());
        getRecyclerview().setAdapter(mCategoryAdapter);
    }

    @Override
    public BasePresenter getPresenter() {
        currentHomeSource = PreferenceUtil.getString(getString(R.string.key_home_sourch), BangumiPresistenceContract.BangumiSource.DILIDLI);
        IBangumiParser parser = null;
        switch (currentHomeSource) {
            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.DILIDLI:
                parser = Dilidili.getInstance();
                break;
        }
        mPresenter = new CategoryPresenter(this, parser);
        return mPresenter;
    }

    @Override
    public void showCategoryItems(List<CategorItem> categorItems) {
        mCategoryAdapter.clearAddAll(categorItems);
    }

    @Override
    public void showResultError(Throwable throwable) {
        showErrorPage();
    }

    @Override
    public void showResultEmpty() {

    }

    @Override
    public void FristLoading() {
        Observable.empty()
                .delay(100, TimeUnit.MILLISECONDS)
                .doOnComplete(() -> mPresenter.getCategoryItems())
                .subscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }
}
