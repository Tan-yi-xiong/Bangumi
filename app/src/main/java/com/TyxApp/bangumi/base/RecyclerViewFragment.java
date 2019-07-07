package com.TyxApp.bangumi.base;

import com.TyxApp.bangumi.R;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public abstract class RecyclerViewFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;



    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefreshLayout;

    @Override
    public int getLayoutId() {
        return R.layout.recyclerview_layout;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }
}
