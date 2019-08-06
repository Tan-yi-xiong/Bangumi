package com.TyxApp.bangumi.main.cachevideo;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface CacheVideoContract {
    interface Presenter extends BasePresenter {
        void getDownoadBangumis();

        void removeDownoadBangumi(Bangumi bangumi);
    }

    interface View extends BaseView {
        void showDownoadBangumis(List<Bangumi> bangumis);

    }
}
