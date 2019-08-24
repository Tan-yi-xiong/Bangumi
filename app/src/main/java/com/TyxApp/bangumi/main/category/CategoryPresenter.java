package com.TyxApp.bangumi.main.category;

import com.TyxApp.bangumi.parse.IHomePageParse;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class CategoryPresenter implements CategoryContract.Presenter {
    private CategoryContract.View mView;
    private IHomePageParse mHomePageParse;
    private CompositeDisposable mDisposable;

    public CategoryPresenter(CategoryContract.View view, IHomePageParse parser) {
        ExceptionUtil.checkNull(view, "view不能为空, CategoryPresenter");
        ExceptionUtil.checkNull(parser, "parser不能为空, CategoryPresenter");
        mHomePageParse = parser;
        mView = view;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getCategoryItems() {
        mDisposable.add(mHomePageParse.getCategorItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categorItems -> mView.showCategoryItems(categorItems),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mDisposable.dispose();
    }
}
