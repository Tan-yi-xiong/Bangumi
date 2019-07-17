package com.TyxApp.bangumi.base;

import android.view.View;

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
    View dataLoadingView;
    @BindView(R.id.error_page)
    View errorPageView;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recyclerview;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void showDataLodaing() {
        errorPageView.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        dataLoadingView.setVisibility(View.VISIBLE);
    }

    public void showRecyclerView() {
        recyclerview.setVisibility(View.VISIBLE);
        dataLoadingView.setVisibility(View.GONE);
        errorPageView.setVisibility(View.GONE);
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public void showErrorPage() {
        recyclerview.setVisibility(View.GONE);
        dataLoadingView.setVisibility(View.GONE);
        errorPageView.setVisibility(View.VISIBLE);
    }

    public View getErrorPageView() {
        return errorPageView;
    }
}
