package com.TyxApp.bangumi.mainpage.homecontent;

import android.util.SparseArray;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.Bangumi;

import java.util.List;

public interface BangumiContract {
    interface View {
        void showHeaderData(List<Bangumi> headerBangumis);

        void showHomeBangumi(List<List<Bangumi>> homeBangumis);

        void showBangumiLoadingError();
    }

    interface Presenter extends BasePresenter {
        void populaterBangumi();
    }
}
