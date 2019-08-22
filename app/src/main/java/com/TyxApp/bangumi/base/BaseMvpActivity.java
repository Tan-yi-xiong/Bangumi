package com.TyxApp.bangumi.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseMvpActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        BasePresenter presenter = getPresenter();
        if (presenter != null) {
            getLifecycle().addObserver(presenter);
        }
        initView(savedInstanceState);
    }

    public BasePresenter getPresenter() {
        return null;
    }

    protected abstract void initView(Bundle savedInstanceState);


    protected abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
