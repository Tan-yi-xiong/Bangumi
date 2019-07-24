package com.TyxApp.bangumi.main.myfavorite;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.myfavorite.adapter.MyFavoriteAdpater;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyFavoriteFragment extends RecyclerViewFragment implements MyFavoriteContract.View {
    private MyFavoriteContract.Presenter mPresenter;
    private MyFavoriteAdpater mAdpater;
    private static final int WHAT = 5;
    private static final int DURATION = 3000;
    @SuppressLint("HandlerLeak")
    private Handler removeBangumiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT) {
                Bangumi bangumi = (Bangumi) msg.obj;
                mPresenter.removeMyFavoriteBangumi(bangumi.getVodId(), bangumi.getVideoSoure());
            }
        }
    };

    @Override
    protected void initView() {
        getRefreshLayout().setEnabled(false);

        mAdpater = new MyFavoriteAdpater(requireActivity());
        mAdpater.setOnItemLongClickLisener(bangumi -> {
            int index = mAdpater.getDataList().indexOf(bangumi);
            mAdpater.remove(index);
            Message message = removeBangumiHandler.obtainMessage();
            message.obj = bangumi;
            message.what = WHAT;
            removeBangumiHandler.sendMessageDelayed(message, DURATION);
            Snackbar.make(getRecyclerview(), bangumi.getName() + "已取消追番", Snackbar.LENGTH_LONG)
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
        mPresenter = new MyFavoritePresenter(this);
        return mPresenter;
    }

    @Override
    public void FristLoading() {
        mPresenter.getMyFavoriteBangumis();
    }

    @Override
    public void showMyFavoriteBangumis(List<Bangumi> bangumis) {
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

    public static MyFavoriteFragment newInstance() {
        return new MyFavoriteFragment();
    }

    @Override
    public void onDestroyView() {
        removeBangumiHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
