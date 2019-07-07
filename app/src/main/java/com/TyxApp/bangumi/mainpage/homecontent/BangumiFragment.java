package com.TyxApp.bangumi.mainpage.homecontent;

import android.os.Bundle;

import com.TyxApp.bangumi.base.BaseFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class BangumiFragment extends RecyclerViewFragment {
    public static BangumiFragment newInstance() {
        return new BangumiFragment();
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

}
