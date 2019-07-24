package com.TyxApp.bangumi.main.bangumi;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.main.bangumi.adapter.BaseHomeBangumiAdapter;
import com.TyxApp.bangumi.main.bangumi.adapter.zzzfun.ZzzFunHomeBangumiAdapter;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.snackbar.Snackbar;
import com.kk.taurus.playerbase.utils.NetworkUtils;

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

        getRecyclerview().setLayoutManager(new GridLayoutManager(getActivity(), 2));

        String honeAdapterSourch = PreferenceUtil.getString(PreferenceUtil.HOME_SOURCE,
                BangumiPresistenceContract.BangumiSource.ZZZFUN);

        switch (honeAdapterSourch) {
            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                mHomeBangumiAdapter = new ZzzFunHomeBangumiAdapter(getContext());
                ZzzFunHomeBangumiAdapter adapter = (ZzzFunHomeBangumiAdapter) mHomeBangumiAdapter;
                getLifecycle().addObserver(adapter);
                break;
        }
        getRecyclerview().setAdapter(mHomeBangumiAdapter);
        getRecyclerview().addItemDecoration(new ZzzFunHomeBangumiAdapter.ItemDecoration());

    }

    @Override
    public BasePresenter getPresenter() {
        mPresenter = new BangumiPresenter(ZzzFun.getInstance(), this);
        return mPresenter;
    }


    @Override
    public void showHomeBangumis(List<List<Bangumi>> homeBangumis) {
        getRefreshLayout().setRefreshing(false);
        mHomeBangumiAdapter.populaterBangumis(homeBangumis);
    }


    @Override
    public void showNewHomeBangumis(List<List<Bangumi>> newHomeBangumis) {
        getRefreshLayout().setRefreshing(false);
        mHomeBangumiAdapter.populaterNewBangumis(newHomeBangumis);
    }

    @Override
    public void showResultError(Throwable throwable) {
        getRefreshLayout().setRefreshing(false);
        int netState = NetworkUtils.getNetworkState(requireContext());
        String snackBarText = "解析发生错误";
        if (netState < 0) {
            snackBarText = "朋友, 你好像没联网";
        }
        Snackbar.make(getRecyclerview(), snackBarText, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showResultEmpty() {

    }
}
