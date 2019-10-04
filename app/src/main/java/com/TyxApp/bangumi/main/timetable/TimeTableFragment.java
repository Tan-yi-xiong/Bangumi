package com.TyxApp.bangumi.main.timetable;

import android.os.Bundle;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.Silisili;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.main.timetable.adapter.TimeTableAdapter;
import com.TyxApp.bangumi.parse.IHomePageParse;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Single;

public class TimeTableFragment extends BaseMvpFragment implements TimeTablecontract.View {
    @BindView(R.id.dateTabs)
    TabLayout dateTablayout;
    @BindView(R.id.rv_date_bangumi)
    RecyclerView mRecyclerView;
    @BindView(R.id.error_page)
    View errorView;

    private TimeTableAdapter mAdapter;
    private boolean isFristLoading;
    private TimeTablecontract.Presenter mPresenter;
    private float mElevation;
    private String[] weeks;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        isFristLoading = true;
        weeks = requireContext().getResources().getStringArray(R.array.week);
        mAdapter = new TimeTableAdapter(requireActivity());
        mRecyclerView.setAdapter(mAdapter);

        AppBarLayout appBarLayout = requireActivity().findViewById(R.id.main_appbar);
        mElevation = appBarLayout.getElevation();
        appBarLayout.setElevation(0);

        errorView.setOnClickListener(v -> mPresenter.getBangumiTimtable());

        for (int i = 0; i < weeks.length; i++) {
            TabLayout.Tab tab = dateTablayout.newTab().setText(weeks[i]);
            tab.setTag(i);
            dateTablayout.addTab(tab);
        }
        dateTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int week = (int) tab.getTag();
                mAdapter.setWeek(week);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public BasePresenter getPresenter() {
        IHomePageParse parser = null;
        String homeSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch), BangumiPresistenceContract.BangumiSource.ZZZFUN);
        switch (homeSourch) {
            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.DILIDLI:
                parser = Dilidili.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.SILISILI:
                parser = Silisili.newInstance();
                break;
        }
        mPresenter = new TimeTablePresenter(this, parser);
        return mPresenter;
    }

    @Override
    public void onResume() {
        if (isFristLoading) {
            Observable.empty()
                    .delay(100, TimeUnit.MILLISECONDS)//延迟100ms再去执行, 否则有卡顿
                    .doOnComplete(() -> mPresenter.getBangumiTimtable())
                    .subscribe();
            isFristLoading = false;
        }
        super.onResume();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragmetn_timetable;
    }

    @Override
    public void showBangumiTimtable(List<List<Bangumi>> timeTableBangumis) {
        mAdapter.clearAddAll(timeTableBangumis);
        if (mRecyclerView.getVisibility() == View.GONE) {
            showRecyclerView();
        }
        SimpleDateFormat format = new SimpleDateFormat("E");
        String today = format.format(System.currentTimeMillis());
        for (int i = 0; i < weeks.length; i++) {
            if (today.equals(weeks[i])) {
                mAdapter.setWeek(i);
                dateTablayout.getTabAt(i).select();
            }
        }
    }

    @Override
    public void showResultError(Throwable throwable) {
        showErrorView();
        Snackbar.make(mRecyclerView, "发生未知错误", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResultEmpty() {

    }

    private void showErrorView() {
        errorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        errorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public static TimeTableFragment newInstance() {
        return new TimeTableFragment();
    }

    @Override
    public void onDestroyView() {
        requireActivity().findViewById(R.id.main_appbar).setElevation(mElevation);
        super.onDestroyView();
    }
}
