package com.TyxApp.bangumi.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.main.bangumi.BangumiFragment;
import com.TyxApp.bangumi.main.cachevideo.CacheVideoFragment;
import com.TyxApp.bangumi.main.category.CategoryFragment;
import com.TyxApp.bangumi.main.favoriteandhistory.FavoriteAndHistoryFragment;
import com.TyxApp.bangumi.main.search.SearchActivity;
import com.TyxApp.bangumi.main.timetable.TimeTableFragment;
import com.TyxApp.bangumi.setting.SettingsActivity;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseMvpActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_navigationview)
    NavigationView mainNavigationview;
    @BindView(R.id.main_drawerlayout)
    DrawerLayout mainDrawerlayout;

    private static final String SF_TASK_NAME = "SF_Task";
    private static final String CURRENT_FRAMENT_KEY = "C_F_K";
    private String currentFragmentName;
    public static final String TAG = "MainActivity";

    private boolean finishFlag;
    private Disposable clearFlagDisposable;

    private static final int DELAYTIME = 2000;
    private String homeSourch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        //initNavigationview
        mainNavigationview.getMenu().getItem(0).setChecked(true);
        mainNavigationview.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        //initToolBar
        setSupportActionBar(mToolbar);

        //initDrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainDrawerlayout, mToolbar, R.string.opendrawer, R.string.closedrawer);
        mainDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        homeSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch), BangumiPresistenceContract.BangumiSource.ZZZFUN);

        //replaceFragment
        currentFragmentName = BangumiFragment.class.getName();
        if (savedInstanceState != null) {
            currentFragmentName = savedInstanceState.getString(CURRENT_FRAMENT_KEY, BangumiFragment.class.getName());
        }
        replaceFragment(currentFragmentName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentSourch = PreferenceUtil.getString(getString(R.string.key_home_sourch), BangumiPresistenceContract.BangumiSource.ZZZFUN);
        if (!currentSourch.equals(homeSourch)) {
            homeSourch = currentSourch;
            replaceFragment(BangumiFragment.class.getName());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mainDrawerlayout.closeDrawer(Gravity.START);
        if (menuItem.getTitle().equals(mainNavigationview.getCheckedItem().getTitle())) {
            return false;
        }
        Observable.just(menuItem.getItemId())
                .delay(180, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemId -> {
                    switch (itemId) {
                        case R.id.nav_bangumi:
                            replaceFragment(BangumiFragment.class.getName());
                            break;
                        case R.id.nav_category:
                            replaceFragment(CategoryFragment.class.getName());

                            break;
                        case R.id.nav_like:
                            replaceFragment(menuItem.getTitle().toString());
                            break;
                        case R.id.nav_history:
                            replaceFragment(menuItem.getTitle().toString());
                            break;
                        case R.id.nav_download:
                            replaceFragment(CacheVideoFragment.class.getName());
                            break;

                        case R.id.nav_setting:
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            break;

                        case R.id.nav_timetable:
                            replaceFragment(TimeTableFragment.class.getName());
                            break;
                    }
                });
        return menuItem.getItemId() != R.id.nav_setting;//设置为不选中
    }

    private void replaceFragment(String tagName) {
        currentFragmentName = tagName;
        if (tagName.equals(BangumiFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.bangumi));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), BangumiFragment.newInstance(), R.id.fl_content);
        } else if (tagName.equals(getString(R.string.myfavorite)) || tagName.equals(getString(R.string.history))) {
            if (tagName.equals(getString(R.string.history))) {
                mainNavigationview.getMenu().findItem(R.id.nav_history).setChecked(true);
            } else {
                mainNavigationview.getMenu().findItem(R.id.nav_like).setChecked(true);
            }
            getSupportActionBar().setTitle(tagName);
            ActivityUtil.replaceFragment(getSupportFragmentManager(), FavoriteAndHistoryFragment.newInstance(tagName), R.id.fl_content);
        } else if (tagName.equals(CategoryFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(1).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.category));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), CategoryFragment.newInstance(), R.id.fl_content);
        } else if (tagName.equals(TimeTableFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(2).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.timetable));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), TimeTableFragment.newInstance(), R.id.fl_content);
        } else if (tagName.equals(CacheVideoFragment.class.getName())) {
            mainNavigationview.getMenu().getItem(4).setChecked(true);
            getSupportActionBar().setTitle(getString(R.string.cache));
            ActivityUtil.replaceFragment(getSupportFragmentManager(), CacheVideoFragment.newInstance(), R.id.fl_content);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            View searchIcon = mToolbar.getChildAt(mToolbar.getChildCount() - 1);
            int centrex = (int) ((searchIcon.getX() + searchIcon.getRight()) / 2);
            int centreY = searchIcon.getHeight() / 2;
            intent.putExtra(SearchActivity.CENTRE_X_KEY, centrex);
            intent.putExtra(SearchActivity.CENTRE_Y_KEY, centreY);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair[0]);
            startActivity(intent, options.toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (!BangumiFragment.class.getName().equals(currentFragmentName)) {
                replaceFragment(BangumiFragment.class.getName());
            } else if (finishFlag) {
                super.onBackPressed();
            } else {
                finishFlag = true;
                Snackbar.make(mToolbar, getString(R.string.snackbar_click_again_finish), Snackbar.LENGTH_SHORT).show();
                clearFlagDisposable = Single.just(0)
                        .delay(2000, TimeUnit.MILLISECONDS)
                        .subscribe(integer -> finishFlag = false);
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAMENT_KEY, currentFragmentName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clearFlagDisposable != null) {
            clearFlagDisposable.dispose();
        }
    }

}
