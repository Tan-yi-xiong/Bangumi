package com.TyxApp.bangumi.downloaddetails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.downloaddetails.adapter.DownloadDetailsAdapter;
import com.TyxApp.bangumi.util.LogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadDetailsFragment extends RecyclerViewFragment implements DownloadDetailsContract.View {
    private DownloadDetailsContract.Presenter mPresenter;
    private DownloadDetailsAdapter mAdapter;
    private int bangumiId;
    private String bangumiSourch;


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getRefreshLayout().setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        getRefreshLayout().setOnRefreshListener(() -> mPresenter.getTasks(bangumiId, bangumiSourch));
        mAdapter = new DownloadDetailsAdapter(requireActivity());
        getRecyclerview().setAdapter(mAdapter);

        mAdapter.setOnItemLongClickLisener(pos -> {
            VideoDownloadTask task = mAdapter.getData(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                    .setTitle("提示")
                    .setMessage("是否删除" + task.getFileName())
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setNeutralButton("确认", (dialog, which) -> {
                        mAdapter.remove(mAdapter.getDataList().indexOf(task));
                        mPresenter.deleteTask(task);
                    });
            builder.show();
            return true;
        });
    }

    @Override
    public BasePresenter getPresenter() {
        bangumiId = requireActivity().getIntent().getIntExtra(DownloadDetailsActivity.ID_KEY, 0);
        bangumiSourch = requireActivity().getIntent().getStringExtra(DownloadDetailsActivity.SOURCH_KEY);
        LogUtil.i(bangumiSourch + bangumiId);
        mPresenter = new DownloadDetailsPresenter(this);
        return mPresenter;
    }

    @Override
    public void FristLoading() {
        mPresenter.getTasks(bangumiId, bangumiSourch);
    }

    public static DownloadDetailsFragment newInstance() {
        return new DownloadDetailsFragment();
    }

    @Override
    public void showTasks(List<VideoDownloadTask> tasks) {
        getRefreshLayout().setRefreshing(false);
        mAdapter.notifyDataSetChanged(tasks);
    }

    @Override
    public void showResultError(Throwable throwable) {
        getRefreshLayout().setRefreshing(false);
        showErrorPage();
    }

    @Override
    public void showResultEmpty() {
        getRefreshLayout().setRefreshing(false);
        showNoResult();
    }

    @Override
    public void onDestroy() {
        mAdapter.unbindService();
        super.onDestroy();
    }
}
