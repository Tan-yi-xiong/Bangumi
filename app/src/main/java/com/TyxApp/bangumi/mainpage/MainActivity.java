package com.TyxApp.bangumi.mainpage;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseActivity;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.ZzzFun;
import com.TyxApp.bangumi.mainpage.homecontent.BangumiFragment;
import com.TyxApp.bangumi.mainpage.search.SearchFragment;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
    @BindView(R.id.tb_main_search)
    Toolbar searchToolBar;
    @BindView(R.id.main_navigationview)
    NavigationView mainNavigationview;
    @BindView(R.id.main_drawerlayout)
    DrawerLayout mainDrawerlayout;

    BangumiFragment mBangumiFragment;
    ActionBarDrawerToggle toggle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        toggle = new ActionBarDrawerToggle(this, mainDrawerlayout, searchToolBar, R.string.opendrawer, R.string.closedrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                mainDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mainDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        };

        mBangumiFragment = (BangumiFragment) ActivityUtil.findFragment(getSupportFragmentManager(), BangumiFragment.class.getName());
        if (mBangumiFragment == null) {
            mBangumiFragment = BangumiFragment.newInstance();
        }
//        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
//                mBangumiFragment, R.id.fl_content);

        //initToolBar
        setSupportActionBar(searchToolBar);
        getSupportActionBar().setTitle(getString(R.string.bangumi));
        searchToolBar.setNavigationOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentByTag(SearchFragment.class.getName());
            if (f == null) {
                mainDrawerlayout.openDrawer(Gravity.START);
            }
        });

        //initDrawerLayout
        mainDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mainDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_searchmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {

        }
        return super.onOptionsItemSelected(item);
    }

}
