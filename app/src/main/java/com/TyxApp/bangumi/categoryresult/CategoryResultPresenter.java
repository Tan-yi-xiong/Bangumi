package com.TyxApp.bangumi.categoryresult;

import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class CategoryResultPresenter implements CategoryResultContract.Presenter {
    private BaseBangumiParser mBangumiParser;
    private CategoryResultContract.View mView;
    private CompositeDisposable mDisposable;

    public CategoryResultPresenter(BaseBangumiParser bangumiParser, CategoryResultContract.View view) {
        ExceptionUtil.checkNull(bangumiParser, "Parser不能为空  CategoryResultPresenter");
        ExceptionUtil.checkNull(view, "view不能为空  CategoryResultPresenter");
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
                        results -> {
                            if (results.isFinalTag()) {
                                mView.showResultEmpty();
                            } else {
                                mView.showNextResult(results.getBangumis());
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
        mBangumiParser.onDestroy();
    }
}
