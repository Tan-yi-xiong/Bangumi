package com.TyxApp.bangumi.mainpage.homecontent;

import android.os.Bundle;
import android.util.SparseArray;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.ZzzFun;
import com.TyxApp.bangumi.util.LogUtil;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.ButterKnife;

public class BangumiFragment extends RecyclerViewFragment implements BangumiContract.View {
    private BangumiPresenter mPresenter;
    private boolean isMainBangumisLodingOver;
    private boolean isHeaderBangumiLoadingOver;
    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }
    private HomeBangumiAdapter mHomeBangumiAdapter;

    @Override
    protected void initView() {
        getRefreshLayout().setRefreshing(true);
        getRefreshLayout().setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        getRefreshLayout().setOnRefreshListener(() -> {
            mPresenter.populaterBangumi();
            isMainBangumisLodingOver = false;
            isHeaderBangumiLoadingOver = false;
        });
        getRecyclerview().setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mHomeBangumiAdapter = new HomeBangumiAdapter(getContext());
        getLifecycle().addObserver(mHomeBangumiAdapter);
        getRecyclerview().setAdapter(mHomeBangumiAdapter);
    }

    @Override
    public BasePresenter getPresenter() {
        mPresenter = new BangumiPresenter<ZzzFun>(new ZzzFun(), this);
        return mPresenter;
    }

    @Override
    public void showHeaderData(List<Bangumi> headerBangumis) {
        isHeaderBangumiLoadingOver = true;
        mHomeBangumiAdapter.headerBangumisAddAll(headerBangumis);
        LogUtil.i(headerBangumis.get(0).getName());
        if (isMainBangumisLodingOver) {
            getRefreshLayout().setRefreshing(false);
        }
    }

    @Override
    public void showHomeBangumi(List<List<Bangumi>> homeBangumis) {
        isMainBangumisLodingOver = true;
        if (isHeaderBangumiLoadingOver) {
            getRefreshLayout().setRefreshing(false);
        }
    }

    @Override
    public void showBangumiLoadingError() {

    }
}
