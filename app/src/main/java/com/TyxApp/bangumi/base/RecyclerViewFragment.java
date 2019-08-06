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
    @BindView(R.id.no_result_view)
    View noResultView;

    private boolean isFristLoading = true;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyclerview;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void showDataLodaing() {
        noResultView.setVisibility(View.GONE);
        errorPageView.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        dataLoadingView.setVisibility(View.VISIBLE);
    }

    public void showRecyclerView() {
        noResultView.setVisibility(View.GONE);
        recyclerview.setVisibility(View.VISIBLE);
        dataLoadingView.setVisibility(View.GONE);
        errorPageView.setVisibility(View.GONE);
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    public void showErrorPage() {
        noResultView.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        dataLoadingView.setVisibility(View.GONE);
        errorPageView.setVisibility(View.VISIBLE);
    }

    public View getErrorPageView() {
        return errorPageView;
    }

    public void showNoResult() {
        noResultView.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.GONE);
        dataLoadingView.setVisibility(View.GONE);
        errorPageView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFristLoading) {
            FristLoading();
            isFristLoading = false;
        }
    }

    public void FristLoading() {

    }
}
