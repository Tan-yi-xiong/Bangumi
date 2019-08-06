package com.TyxApp.bangumi.categoryresult;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.util.ActivityUtil;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

public class CategoryResultActivity extends BaseMvpActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Fragment mContentFragment;

    public static final String CATEGORYWORD_KEY = "C_W_K";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setTitle(getIntent().getStringExtra(CategoryResultActivity.CATEGORYWORD_KEY));

        mContentFragment = ActivityUtil.findFragment(getSupportFragmentManager(), CategoryResultFragment.class.getName());
        if (mContentFragment == null) {
            mContentFragment = CategoryResultFragment.newInstance();
        }
        ActivityUtil.replaceFragment(getSupportFragmentManager(), mContentFragment, R.id.content_frame);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_base;
    }

    public static void startCategoryResultActivity(Context context, String categoryWord) {
        Intent intent = new Intent(context, CategoryResultActivity.class);
        intent.putExtra(CATEGORYWORD_KEY, categoryWord);
        context.startActivity(intent);
    }

}
