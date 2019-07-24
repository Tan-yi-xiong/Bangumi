package com.TyxApp.bangumi.main.myfavorite;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;

public interface MyFavoriteContract {
    interface Presenter extends BasePresenter {
        void getMyFavoriteBangumis();

        void removeMyFavoriteBangumi(int id, String source);
    }

    interface View extends BaseView {
        void showMyFavoriteBangumis(List<Bangumi> bangumis);
    }
}
