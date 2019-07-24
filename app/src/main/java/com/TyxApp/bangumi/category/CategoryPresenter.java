package com.TyxApp.bangumi.category;

import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class CategoryPresenter implements CategoryContract.Presenter {
    private BaseBangumiParser mBangumiParser;
    private CategoryContract.View mView;
    private CompositeDisposable mDisposable;

    public CategoryPresenter(BaseBangumiParser bangumiParser, CategoryContract.View view) {
        ExceptionUtil.checkNull(bangumiParser, "Parser不能为空  CategoryPresenter");
        ExceptionUtil.checkNull(view, "view不能为空  CategoryPresenter");
        mBangumiParser = bangumiParser;
        mView = view;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getResult(String category) {
        mDisposable.add(mBangumiParser.getCategoryBangumis(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showResult(bangumis),
                        throwable -> mView.showResultError(throwable)));

    }

    @Override
    public void getNextResult() {
        mDisposable.add(mBangumiParser.getNextCategoryBangumis()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bangumis -> mView.showNextResult(bangumis),
                        throwable -> mView.showResultError(throwable),
                        () -> mView.showResultEmpty()));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mDisposable.dispose();
        mBangumiParser.onDestroy();
    }
}
