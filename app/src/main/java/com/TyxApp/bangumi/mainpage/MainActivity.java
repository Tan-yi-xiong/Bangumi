package com.TyxApp.bangumi.mainpage;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.mainpage.homecontent.BangumiFragment;
import com.TyxApp.bangumi.mainpage.search.SearchFragment;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;

public class MainActivity extends BaseMvpActivity {
    @BindView(R.id.tb_main_search)
    Toolbar searchToolBar;
    @BindView(R.id.main_navigationview)
    NavigationView mainNavigationview;
    @BindView(R.id.main_drawerlayout)
    DrawerLayout mainDrawerlayout;

    private static final String SF_TASK_NAME = "SF_Task";
    public static final String TAG = "MainActivity";
    private MenuItem searchMenu;
    private BangumiFragment mBangumiFragment;
    private ActionBarDrawerToggle toggle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //Fragmet返回栈空时恢复主界面ToolBar原样
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (!searchMenu.isVisible()) {
                    searchMenu.setVisible(true);
                    AnimationUtil.ActionBarDrawerToggleAnimation(toggle, false);
                }
            }
        });

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

        mBangumiFragment = (BangumiFragment) ActivityUtil.findFragment(
                getSupportFragmentManager(), BangumiFragment.class.getName());

        if (mBangumiFragment == null) {
            mBangumiFragment = BangumiFragment.newInstance();
        }
        ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),
                mBangumiFragment, R.id.fl_content);

        //initToolBar
        setSupportActionBar(searchToolBar);
        getSupportActionBar().setTitle(getString(R.string.bangumi));
        searchToolBar.setNavigationOnClickListener(v -> {
            //Fragmet返回栈空时当侧栏开关, 反则当返回按钮
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                mainDrawerlayout.openDrawer(Gravity.START);
            } else {
                onBackPressed();
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
        searchMenu = menu.findItem(R.id.action_search);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            ActivityUtil.addFragmentToBackTask(getSupportFragmentManager(), SearchFragment.newInstance(),
                    R.id.fl_content, FragmentTransaction.TRANSIT_FRAGMENT_FADE, SF_TASK_NAME);

            searchMenu.setVisible(false);
            AnimationUtil.ActionBarDrawerToggleAnimation(toggle, true);
        }
        return super.onOptionsItemSelected(item);
    }

}
