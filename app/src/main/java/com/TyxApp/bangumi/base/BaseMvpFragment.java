package com.TyxApp.bangumi.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.TyxApp.bangumi.util.ExceptionUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseMvpFragment extends Fragment {
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ExceptionUtil.checkNull(getPresenter(), "presenter不能为空");
        BasePresenter presenter = getPresenter();
        getLifecycle().addObserver(presenter);
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    protected abstract void initView();


    public abstract BasePresenter getPresenter();

    public abstract int getLayoutId();

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }
}