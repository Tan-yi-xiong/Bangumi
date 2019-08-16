package com.TyxApp.bangumi.main.favoriteandhistory;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface FavoriteAndHistoryContract {
    interface Presenter extends BasePresenter {
        void getBangumis();

        void removeBangumi(String id, String source);

        void revocationRemoveBangumi(Bangumi bangumi);
    }

    interface View extends BaseView {
        void showBangumis(List<Bangumi> bangumis);
    }
}
