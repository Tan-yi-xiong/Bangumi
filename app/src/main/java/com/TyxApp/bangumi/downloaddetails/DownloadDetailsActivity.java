package com.TyxApp.bangumi.downloaddetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadDetailsActivity extends BaseMvpActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public static final String ID_KEY = "I_K";
    public static final String SOURCH_KEY = "S_K";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("下载详情");
        toolbar.setNavigationOnClickListener(v -> finish());


        Fragment downloadDetailsFragment = ActivityUtil.findFragment(getSupportFragmentManager(), DownloadDetailsFragment.class.getName());
        if (downloadDetailsFragment == null) {
            downloadDetailsFragment = DownloadDetailsFragment.newInstance();
        }
        ActivityUtil.replaceFragment(getSupportFragmentManager(), downloadDetailsFragment, R.id.content_frame);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_base;
    }

    public static void startDownloadDetailsActivity(Context context, int bangumiId, String bangumiSourch) {
        Intent intent = new Intent(context, DownloadDetailsActivity.class);
        intent.putExtra(ID_KEY, bangumiId);
        intent.putExtra(SOURCH_KEY, bangumiSourch);
        context.startActivity(intent);
    }

}
