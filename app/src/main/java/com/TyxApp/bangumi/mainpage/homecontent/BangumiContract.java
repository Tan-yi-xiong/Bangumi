package com.TyxApp.bangumi.mainpage.homecontent;

import android.util.SparseArray;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.Bangumi;

import java.util.List;

public interface BangumiContract {
    interface View {
        void showHomeBangumis(List<List<Bangumi>> homeBangumis);

        void showBangumiLoadingError(Throwable throwable);

        void showNewHomeBangumis(List<List<Bangumi>> newHomeBangumis);
    }

    interface Presenter extends BasePresenter {
        void populaterBangumi();

        void refreshHomeData();
    }
}
