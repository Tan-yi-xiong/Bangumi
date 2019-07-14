package com.TyxApp.bangumi.base;

import android.view.View;
import android.widget.ProgressBar;

import com.TyxApp.bangumi.R;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public abstract class RecyclerViewFragment extends BaseMvpFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.pb_data_loading)
    ProgressBar dataLoadingView;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recyclerview;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void showDataLodaing() {
        recyclerview.setVisibility(View.GONE);
        dataLoadingView.setVisibility(View.VISIBLE);
    }

    public void showRecyclerView() {
        recyclerview.setVisibility(View.VISIBLE);
        dataLoadingView.setVisibility(View.GONE);
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }
}
