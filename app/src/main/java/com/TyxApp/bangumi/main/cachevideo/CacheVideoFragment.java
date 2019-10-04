package com.TyxApp.bangumi.main.cachevideo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.cachevideo.adapter.CacheVideoAdapter;

import java.util.List;

public class CacheVideoFragment extends RecyclerViewFragment implements CacheVideoContract.View {
    private CacheVideoContract.Presenter mPresenter;
    private CacheVideoAdapter mAdapter;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getRefreshLayout().setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        getRefreshLayout().setOnRefreshListener(() -> mPresenter.getDownoadBangumis());

        mAdapter = new CacheVideoAdapter(requireActivity());
        mAdapter.setOnItemLongClickLisener(pos -> {
            Bangumi bangumi = mAdapter.getData(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                    .setTitle("提示")
                    .setMessage("是否删除" + bangumi.getName() + "所有缓存视频")
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNeutralButton("确认", (dialog, which) -> mPresenter.removeDownoadBangumi(bangumi));
            builder.show();
            return true;
        });
        getRecyclerview().setAdapter(mAdapter);
    }

    @Override
    public BasePresenter getPresenter() {
        mPresenter = new CacheVideoPresenter(this);
        return mPresenter;
    }

    @Override
    public void FristLoading() {
        mPresenter.getDownoadBangumis();
    }

    @Override
    public void showDownoadBangumis(List<Bangumi> bangumis) {
        mAdapter.clearAddAll(bangumis);
        getRefreshLayout().setRefreshing(false);
    }

    @Override
    public void showResultError(Throwable throwable) {
        showErrorPage();
        getRefreshLayout().setRefreshing(false);
    }

    @Override
    public void showResultEmpty() {
        showNoResult();
        getRefreshLayout().setRefreshing(false);
    }

    public static CacheVideoFragment newInstance() {
        return new CacheVideoFragment();
    }
}
