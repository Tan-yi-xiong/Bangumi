package com.TyxApp.bangumi.main.timetable;

import com.TyxApp.bangumi.data.source.remote.IBangumiParser;
import com.TyxApp.bangumi.util.ExceptionUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class TimeTablePresenter implements TimeTablecontract.Presenter {
    private TimeTablecontract.View mView;
    private CompositeDisposable mDisposable;
    private IBangumiParser mParser;

    public TimeTablePresenter(TimeTablecontract.View view, IBangumiParser parser) {
        ExceptionUtil.checkNull(view, "view不能为空 TimeTablePresenter");
        ExceptionUtil.checkNull(parser, "parser不能为空 TimeTablePresenter");
        mView = view;
        mParser = parser;
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void getBangumiTimtable() {
        mDisposable.add(mParser.getBangumiTimeTable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        timeTableBangumis -> mView.showBangumiTimtable(timeTableBangumis),
                        throwable -> mView.showResultError(throwable)));
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {
        mDisposable.dispose();
        mView = null;
    }
}
