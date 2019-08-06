package com.TyxApp.bangumi.main.favoriteandhistory;

import android.os.Bundle;

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

import io.reactivex.disposables.Disposable;

public class FavoriteAndHistoryFragment extends RecyclerViewFragment implements FavoriteAndHistoryContract.View {
    private FavoriteAndHistoryContract.Presenter mPresenter;
    private static final String MEUN_TAG = "M_T";
    private FavoriteAndHistoryAdpater mAdpater;
    private Snackbar mSnackbar;


    @Override
    protected void initView(Bundle savedInstanceState) {
        getRefreshLayout().setEnabled(false);

        mAdpater.setOnItemLongClickLisener(pos -> {
            Bangumi bangumi = mAdpater.getData(pos);
            mAdpater.remove(pos);
            mPresenter.removeBangumi(bangumi.getVodId(), bangumi.getVideoSoure());

            mSnackbar = Snackbar.make(getRecyclerview(), bangumi.getName() + "已移除", Snackbar.LENGTH_SHORT);
                    mSnackbar.setAction("撤销", v -> {
                        mPresenter.revocationRemoveBangumi(bangumi);
                        mAdpater.addInserted(bangumi, pos);
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
        if (mSnackbar != null) {
            if (mSnackbar.isShown()) {
                mSnackbar.dismiss();
            }
        }
        super.onDestroyView();
    }
}
