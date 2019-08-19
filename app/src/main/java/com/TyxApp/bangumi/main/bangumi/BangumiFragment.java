package com.TyxApp.bangumi.main.bangumi;

import android.os.Bundle;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.Silisili;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.main.bangumi.adapter.DefaultHomeAdapter;
import com.TyxApp.bangumi.main.bangumi.adapter.BaseHomeAdapter;
import com.TyxApp.bangumi.main.bangumi.adapter.Dilidili.DilidiliHomeAdapter;
import com.TyxApp.bangumi.main.bangumi.adapter.silisili.SilisiliAdapter;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.snackbar.Snackbar;
import com.kk.taurus.playerbase.utils.NetworkUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearSnapHelper;

import io.reactivex.Observable;
import io.reactivex.Single;

public class BangumiFragment extends RecyclerViewFragment implements BangumiContract.View {
    private BangumiPresenter mPresenter;

    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }

    private BaseHomeAdapter mHomeAdapter;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getRecyclerview().setAdapter(mHomeAdapter);
        getRefreshLayout().setRefreshing(true);
        getRefreshLayout().setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        getRefreshLayout().setOnRefreshListener(() -> mPresenter.refreshHomeData());

        getErrorPageView().setOnClickListener(v -> {
            getRefreshLayout().setRefreshing(true);
            mPresenter.populaterBangumi();
        });
    }

    @Override
    public BasePresenter getPresenter() {
        String homeSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch),
                BangumiPresistenceContract.BangumiSource.ZZZFUN);

        switch (homeSourch) {
            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                mPresenter = new BangumiPresenter(ZzzFun.getInstance(), this);
                mHomeAdapter = new DefaultHomeAdapter(requireActivity());
                break;

            case BangumiPresistenceContract.BangumiSource.DILIDLI:
                mPresenter = new BangumiPresenter(Dilidili.getInstance(), this);
                mHomeAdapter = new DilidiliHomeAdapter(requireActivity());
                break;

            case BangumiPresistenceContract.BangumiSource.SILISILI:
                mPresenter = new BangumiPresenter(Silisili.newInstance(), this);
                mHomeAdapter = new SilisiliAdapter(requireActivity());
                break;
        }
        return mPresenter;
    }

    @Override
    public void FristLoading() {
        mPresenter.populaterBangumi();
    }

    @Override
    public void showHomeBangumis(Map<String, List<Bangumi>> homeBangumis) {
        getRefreshLayout().setRefreshing(false);
        showRecyclerView();
        mHomeAdapter.populaterBangumis(homeBangumis);
    }


    @Override
    public void showResultError(Throwable throwable) {
        if (getRefreshLayout() != null) {
            getRefreshLayout().setRefreshing(false);
        }
        int netState = NetworkUtils.getNetworkState(requireContext());
        if (mHomeAdapter.getItemCount() == 0) {
            showErrorPage();
        }
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
