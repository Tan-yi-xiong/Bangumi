package com.TyxApp.bangumi.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.main.bangumi.BangumiFragment;
import com.TyxApp.bangumi.main.history.HistoryFragment;
import com.TyxApp.bangumi.main.myfavorite.MyFavoriteFragment;
import com.TyxApp.bangumi.main.search.SearchFragment;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;

public class MainActivity extends BaseMvpActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.tb_main_search)
    Toolbar searchToolBar;
    @BindView(R.id.main_navigationview)
    NavigationView mainNavigationview;
    @BindView(R.id.main_drawerlayout)
    DrawerLayout mainDrawerlayout;

    private static final String SF_TASK_NAME = "SF_Task";
    private static final String CURRENT_FRAMENT_KEY = "C_F_K";
    private String currentFragmentName;
    public static final String TAG = "MainActivity";
    private MenuItem searchMenu;
    private ActionBarDrawerToggle toggle;

    private boolean isFinish;
    private static final int DELAYTIME = 2000;
    private Handler doubleBackPressed = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    isFinish = false;
                }
            };

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

        //initNavigationview
        mainNavigationview.getMenu().getItem(0).setChecked(true);
        mainNavigationview.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        //initToolBar
        setSupportActionBar(searchToolBar);
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

        //replaceFragment
        currentFragmentName = BangumiFragment.class.getName();
        if (savedInstanceState != null) {
            currentFragmentName = savedInstanceState.getString(CURRENT_FRAMENT_KEY, BangumiFragment.class.getName());
        }
        replaceFragment(currentFragmentName);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mainDrawerlayout.closeDrawer(Gravity.START);
        switch (menuItem.getItemId()) {
            case R.id.nav_bangumi:
                replaceFragment(BangumiFragment.class.getName());
                break;
            case R.id.nav_category:

                break;
            case R.id.nav_like:
                replaceFragment(MyFavoriteFragment.class.getName());
                break;
            case R.id.nav_history:
                replaceFragment(HistoryFragment.class.getName());
                break;
            case R.id.nav_download:

                break;
            case R.id.nav_about:

                break;

            case R.id.nav_setting:

                break;
        }
        return true;
    }

    private void replaceFragment(String fragmentName) {
        currentFragmentName = fragmentName;
        if (fragmentName.equals(BangumiFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.bangumi));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), BangumiFragment.newInstance(), R.id.fl_content);
        } else if (fragmentName.equals(MyFavoriteFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(2).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.myfavorite));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), MyFavoriteFragment.newInstance(), R.id.fl_content);
        } else if(fragmentName.equals(HistoryFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(4).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.history));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), HistoryFragment.newInstance(), R.id.fl_content);
        }
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

    @Override
    public void onBackPressed() {
        if (!BangumiFragment.class.getName().equals(currentFragmentName)) {
            replaceFragment(BangumiFragment.class.getName());
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (!isFinish) {
                isFinish = true;
                doubleBackPressed.sendEmptyMessageDelayed(0, DELAYTIME);
                Snackbar.make(mainDrawerlayout, "再按一次退出", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        doubleBackPressed.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAMENT_KEY, currentFragmentName);
    }
}
