package com.TyxApp.bangumi.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
}
