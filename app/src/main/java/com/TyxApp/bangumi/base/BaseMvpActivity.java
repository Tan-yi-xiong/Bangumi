package com.TyxApp.bangumi.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

public abstract class BaseMvpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
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
}
