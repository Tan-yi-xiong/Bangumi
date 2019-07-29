package com.TyxApp.bangumi.categoryresult;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;


public interface CategoryResultContract {
    interface Presenter extends BasePresenter {
       void getResult(String category);

       void getNextResult();
    }

    interface View extends BaseView {
       void showResult(List<Bangumi> results);

       void showNextResult(List<Bangumi> results);
    }
}
