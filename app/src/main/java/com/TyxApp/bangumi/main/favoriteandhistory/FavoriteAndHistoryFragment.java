package com.TyxApp.bangumi.main.favoriteandhistory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.favoriteandhistory.adapter.FavoriteAndHistoryAdpater;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteAndHistoryFragment extends RecyclerViewFragment implements FavoriteAndHistoryContract.View {
    private FavoriteAndHistoryContract.Presenter mPresenter;
    private static final String MEUN_TAG = "M_T";
    private FavoriteAndHistoryAdpater mAdpater;
    private static final int WHAT = 5;
    private static final int DURATION = 3000;
    @SuppressLint("HandlerLeak")
    private Handler removeBangumiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT) {
                Bangumi bangumi = (Bangumi) msg.obj;
                mPresenter.removeBangumi(bangumi.getVodId(), bangumi.getVideoSoure());
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        getRefreshLayout().setEnabled(false);

        mAdpater.setOnItemLongClickLisener(bangumi -> {
            int index = mAdpater.getDataList().indexOf(bangumi);
            mAdpater.remove(index);
            Message message = removeBangumiHandler.obtainMessage();
            message.obj = bangumi;
            message.what = WHAT;
            removeBangumiHandler.sendMessageDelayed(message, DURATION);
            Snackbar.make(getRecyclerview(), bangumi.getName() + "已移除", Snackbar.LENGTH_LONG)
                    .setAction("撤销", v -> {
                        removeBangumiHandler.removeMessages(WHAT);
                        mAdpater.addInserted(bangumi, index);
                    })
                    .show();
            return true;
        });
        getRecyclerview().setAdapter(mAdpater);
        getRecyclerview().setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
    }

    @Override
    public BasePresenter getPresenter() {
        String tag = getArguments().getString(MEUN_TAG);
        mAdpater = new FavoriteAndHistoryAdpater(requireActivity());
        if (getString(R.string.myfavorite).equals(tag)) {
            mAdpater.setShowTime(false);
            mPresenter = new FavoritePresenter(this);
        } else if (getString(R.string.history).equals(tag)) {
            mAdpater.setShowTime(true);
            mPresenter = new HistoryPresenter(this);
        }
        return mPresenter;
    }

    @Override
    public void FristLoading() {
        mPresenter.getBangumis();
    }

    @Override
    public void showBangumis(List<Bangumi> bangumis) {
        mAdpater.clearAddAll(bangumis);
    }

    @Override
    public void showResultError(Throwable throwable) {
        LogUtil.i(throwable.toString());
    }

    @Override
    public void showResultEmpty() {
        showNoResult();
    }

    public static FavoriteAndHistoryFragment newInstance(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(MEUN_TAG, tag);
        FavoriteAndHistoryFragment fragment = new FavoriteAndHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        removeBangumiHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
