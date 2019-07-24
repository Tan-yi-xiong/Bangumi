package com.TyxApp.bangumi.main.bangumi;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface BangumiContract {
    interface View extends BaseView {
        void showHomeBangumis(List<List<Bangumi>> homeBangumis);


        void showNewHomeBangumis(List<List<Bangumi>> newHomeBangumis);
    }

    interface Presenter extends BasePresenter {
        void populaterBangumi();

        void refreshHomeData();
    }
}
