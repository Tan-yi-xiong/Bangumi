package com.TyxApp.bangumi.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.util.ActivityUtil;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

public class CategoryActivity extends BaseMvpActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Fragment mContentFragment;

    public static final String CATEGORYWORD_KEY = "C_W_K";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(getIntent().getStringExtra(CategoryActivity.CATEGORYWORD_KEY));

        mContentFragment = ActivityUtil.findFragment(getSupportFragmentManager(), CategoryFragment.class.getName());
        if (mContentFragment == null) {
            mContentFragment = CategoryFragment.newInstance();
        }
        ActivityUtil.replaceFragment(getSupportFragmentManager(), mContentFragment, R.id.content_frame);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_base;
    }

    public static void startCategoryActivity(Context context, String categoryWord) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(CATEGORYWORD_KEY, categoryWord);
        context.startActivity(intent);
    }

}
