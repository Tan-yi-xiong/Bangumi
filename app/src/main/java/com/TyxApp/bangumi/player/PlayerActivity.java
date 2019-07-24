package com.TyxApp.bangumi.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.util.ActivityUtil;

import androidx.core.app.ActivityOptionsCompat;

public class PlayerActivity extends BaseMvpActivity {

    public static final String INTENT_KEY = "bangumi_key";
    private PlayerFragment mPlayerFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPlayerFragment = (PlayerFragment) ActivityUtil.findFragment(
                getSupportFragmentManager(),
                PlayerFragment.class.getName());
        if (mPlayerFragment == null) {
            mPlayerFragment = PlayerFragment.newInstance();
        }

        ActivityUtil.replaceFragment(
                getSupportFragmentManager(),
                mPlayerFragment,
                R.id.fl_playercontent);
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

    public static void startPlayerActivityWithTransition(Activity activity, Bangumi bangumi, ActivityOptionsCompat compat) {
        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.putExtra(INTENT_KEY, bangumi);
        activity.startActivity(intent, compat.toBundle());
    }
}
