package com.TyxApp.bangumi.categoryresult;

import com.TyxApp.bangumi.parse.IHomePageParse;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class CategoryResultPresenter implements CategoryResultContract.Presenter {
    private IHomePageParse mHomePageParse;
    private CategoryResultContract.View mView;
    private CompositeDisposable mDisposable;

    public CategoryResultPresenter(IHomePageParse homePageParse, CategoryResultContract.View view) {
        ExceptionUtil.checkNull(homePageParse, "Parser不能为空  CategoryResultPresenter");
        ExceptionUtil.checkNull(view, "view不能为空  CategoryResultPresenter");
        mHomePageParse = homePageParse;
        mView = view;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getResult(String category) {
        mDisposable.add(mHomePageParse.getCategoryBangumis(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showResult(bangumis),
                        throwable -> mView.showResultError(throwable)));

    }

    @Override
    public void getNextResult() {
        mDisposable.add(mHomePageParse.getNextCategoryBangumis()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isNull()) {
                                mView.showResultEmpty();
                            } else {
                                mView.showNextResult(results.getResult());
                            }
                        },
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
