package com.TyxApp.bangumi.main.history;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface HistoryContract {
    interface Presenter extends BasePresenter {
        void getHistoryBangumis();

        void removeHistoryBangumi(int id, String source);
    }

    interface View extends BaseView {
        void showHistoryBangumis(List<Bangumi> bangumis);
    }
}
