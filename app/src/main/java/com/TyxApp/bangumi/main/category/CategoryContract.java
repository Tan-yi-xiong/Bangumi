package com.TyxApp.bangumi.main.category;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.CategorItem;

import java.util.List;

public interface CategoryContract {
    interface Presenter extends BasePresenter {
        void getCategoryItems();

    }

    interface View extends BaseView {
        void showCategoryItems(List<CategorItem> categorItems);
    }
}
