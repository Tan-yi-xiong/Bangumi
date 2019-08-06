package com.TyxApp.bangumi.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.util.ActivityUtil;

public class PlayerActivity extends BaseMvpActivity {

    public static final String INTENT_KEY = "intent_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Fragment fragment;
        fragment = ActivityUtil.findFragment(getSupportFragmentManager(), RemotePlayerFragment.class.getName());
        if (fragment == null) {
            fragment = ActivityUtil.findFragment(getSupportFragmentManager(), LocalPlayerFragment.class.getName());
            if (fragment == null) {
                if (getIntent().getParcelableExtra(INTENT_KEY) != null) {
                    fragment = RemotePlayerFragment.newInstance();
                } else {
                    fragment = LocalPlayerFragment.newInstance();
                }
            }
        }
        ActivityUtil.replaceFragment(getSupportFragmentManager(), fragment, R.id.fl_playercontent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_player;
    }

    public static void startPlayerActivity(Context context, Bangumi bangumi) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(INTENT_KEY, bangumi);
        context.startActivity(intent);
    }

    public static void startPlayerActivity(Context context, String filePath) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(INTENT_KEY, filePath);
        context.startActivity(intent);
    }
}
