package com.TyxApp.bangumi.main.timetable;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface TimeTablecontract {
    interface Presenter extends BasePresenter {
        void getBangumiTimtable();
    }

    interface View extends BaseView {
        void showBangumiTimtable(List<List<Bangumi>> timeTableBangumis);
    }
}
