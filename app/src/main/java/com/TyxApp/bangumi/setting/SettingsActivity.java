package com.TyxApp.bangumi.setting;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.util.ActivityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseMvpActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.setting));
        toolbar.setNavigationOnClickListener(v -> finish());

        Fragment fragment = ActivityUtil.findFragment(getSupportFragmentManager(), SettingsFragment.class.getName());
        if (fragment == null) {
            fragment = SettingsFragment.newInstance();
        }
        ActivityUtil.replaceFragment(getSupportFragmentManager(), fragment, R.id.content_frame);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_base;
    }

}