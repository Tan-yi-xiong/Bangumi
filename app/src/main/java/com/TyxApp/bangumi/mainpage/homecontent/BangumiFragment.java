package com.TyxApp.bangumi.mainpage.homecontent;

import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.ZzzFun;
import com.TyxApp.bangumi.mainpage.homecontent.adapter.BaseHomeBangumiAdapter;
import com.TyxApp.bangumi.mainpage.homecontent.adapter.HomeBangumiAdapter;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

public class BangumiFragment extends RecyclerViewFragment implements BangumiContract.View {
    private BangumiPresenter mPresenter;
    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }
    private BaseHomeBangumiAdapter mHomeBangumiAdapter;

    @Override
    protected void initView() {
        getRefreshLayout().setRefreshing(true);
        getRefreshLayout().setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        getRefreshLayout().setOnRefreshListener(() -> mPresenter.refreshHomeData());

        getRecyclerview().setVisibility(View.GONE);//刚开始不显示, 等加载完数据在显示
        getRecyclerview().setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //未加切换逻辑
        mHomeBangumiAdapter = new HomeBangumiAdapter(getContext());
        getLifecycle().addObserver((HomeBangumiAdapter)mHomeBangumiAdapter);

        getRecyclerview().setAdapter(mHomeBangumiAdapter);
        getRecyclerview().addItemDecoration(new HomeBangumiAdapter.ItemDecoration());
    }

    @Override
    public BasePresenter getPresenter() {
        mPresenter = new BangumiPresenter<ZzzFun>(new ZzzFun(), this);
        return mPresenter;
    }


    @Override
    public void showHomeBangumis(List<List<Bangumi>> homeBangumis) {
        getRefreshLayout().setRefreshing(false);
        getRecyclerview().setVisibility(View.VISIBLE);
        mHomeBangumiAdapter.populaterBangumis(homeBangumis);
    }

    @Override
    public void showBangumiLoadingError(Throwable throwable) {
        LogUtil.i(throwable.toString());
        Snackbar.make(getRecyclerview(), throwable.toString(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNewHomeBangumis(List<List<Bangumi>> newHomeBangumis) {
        getRefreshLayout().setRefreshing(false);
        mHomeBangumiAdapter.populaterNewBangumis(newHomeBangumis);
    }
}
