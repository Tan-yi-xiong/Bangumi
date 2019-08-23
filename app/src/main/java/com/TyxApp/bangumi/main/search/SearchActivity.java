package com.TyxApp.bangumi.main.search;

import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.main.search.SearchResult.SearchResultFragmetnAdministrator;
import com.TyxApp.bangumi.main.search.adapter.SearchAdapter;
import com.TyxApp.bangumi.main.search.transitions.CircularReveal;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.util.TransitionUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchActivity extends BaseMvpActivity implements SearchContract.View {

    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.searchHistory)
    RecyclerView mRecyclerView;
    @BindView(R.id.scrim)
    View scrim;
    @BindView(R.id.searchBar_line)
    View line;

    private SearchPresenter mPresenter;
    private SearchAdapter mAdapter;
    private EditText searchEditText;
    private static final int FADE_ANIMATION_DURATION = 150;
    public static final String CENTRE_X_KEY = "CENTER_X_KEY";
    public static final String CENTRE_Y_KEY = "CENTER_Y_KEY";

    public BasePresenter getPresenter() {
        mPresenter = new SearchPresenter(this);
        return mPresenter;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Fade fade = new Fade();
        fade.setDuration(200);
        fade.addTarget(scrim);
        fade.addTarget(findViewById(R.id.searchBar_background));
        getWindow().setReturnTransition(fade);//不知为毛我在xml设置它不执行动画, 所以代码设置。
        Fragment fragment = ActivityUtil.findFragment(getSupportFragmentManager(), SearchResultFragmetnAdministrator.class.getName());
        if (fragment != null) {
            ActivityUtil.replaceFragment(getSupportFragmentManager(), fragment, R.id.searchResult);
        }
        initTransition();
        initSearchView();
        initRecyclerView();
    }

    @OnClick({R.id.scrim, R.id.back})
    public void onClick(View view) {
        onBackPressed();
    }

    private void initRecyclerView() {
        mAdapter = new SearchAdapter(this);
        mAdapter.setOnItemClickListener(position -> {
            creatSearchResultFragmetn();
            String word = mAdapter.getData(position).getWord();
            searchEditText.setText(word);
            hindSearchState(true);
        });
        mAdapter.setOnItemLongClickLisener(position -> {
            SearchWord searchWord = mAdapter.getData(position);
            mPresenter.removeWord(searchWord);
            mAdapter.remove(position);
            AnimationUtil.unfoldIncrease(mRecyclerView);//绘制减少的高度
            return true;
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void initSearchView() {
        searchView.setIconifiedByDefault(false);
        int id = searchView.getResources().getIdentifier("android:id/search_src_text", null, null);
        searchEditText = searchView.findViewById(id);
        searchEditText.setTextSize(16);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.grey_600));
        //隐藏搜索图标
        int searchIconId = searchView.getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView imageView = searchView.findViewById(searchIconId);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxWidth(0);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        imageView.setImageDrawable(null);

        //隐藏下划线
        int inPutLine = searchView.getResources().getIdentifier("android:id/search_plate", null, null);
        View view = searchView.findViewById(inPutLine);
        view.setBackgroundColor(Color.TRANSPARENT);

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard();
                showSearchState();
            } else {
                hindKeyboard();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    return false;
                } else {
                    creatSearchResultFragmetn();
                    searchView.clearFocus();
                    hindSearchState(true);
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPresenter.getSimilarityWords(newText);
                return true;
            }
        });
    }

    private void initTransition() {
        int centreX = getIntent().getIntExtra(CENTRE_X_KEY, -1);
        int centerY = getIntent().getIntExtra(CENTRE_Y_KEY, -1);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                if (centerY == -1 || centreX == -1) {
                    return;
                }
                Point centre = new Point(centreX, centerY);
                CircularReveal circularReveal = (CircularReveal) TransitionUtils.findTransition((TransitionSet) getWindow().getEnterTransition(), CircularReveal.class, R.id.searchBar);
                circularReveal.setCenter(centre);
            }
        });
        getWindow().getEnterTransition().addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                searchView.requestFocus();
                mPresenter.getWords();
                showKeyboard();
            }
        });
        getWindow().getReturnTransition().addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                if (centerY == -1 || centreX == -1) {
                    return;
                }
                View view;
                if (getSupportFragmentManager().getFragments().size() > 0) {
                    view = findViewById(R.id.searchParent);
                } else {
                    view = findViewById(R.id.searchBar);
                }
                ViewAnimationUtils.createCircularReveal(view,
                        centreX,
                        centerY,
                        view.getHeight() > view.getWidth() ? view.getHeight() : view.getWidth(),
                        0).start();
            }
        });
    }

    private void showKeyboard() {
        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.showSoftInput(searchEditText, 0);
    }

    private void hindKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showSearchState() {
        Fragment fragment = ActivityUtil.findFragment(getSupportFragmentManager(), SearchResultFragmetnAdministrator.class.getName());
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                    .commit();
        }
        if (mRecyclerView.getVisibility() == View.GONE) {
            line.setVisibility(View.VISIBLE);
            AnimationUtil.unfold(mRecyclerView);
        }
        if (scrim.getVisibility() == View.GONE) {
            AnimationUtil.fadeIn(scrim, FADE_ANIMATION_DURATION);
        }
    }

    private void hindSearchState(boolean setMaxLifecycle) {
        if (setMaxLifecycle) {
            Fragment fragment = ActivityUtil.findFragment(getSupportFragmentManager(), SearchResultFragmetnAdministrator.class.getName());
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                        .commit();
            }
            mPresenter.saveWord(searchEditText.getText().toString());
        }
        searchView.clearFocus();
        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            AnimationUtil.shrink(mRecyclerView);
            line.setVisibility(View.INVISIBLE);
        }
        if (scrim.getVisibility() == View.VISIBLE) {
            AnimationUtil.fadeOut(scrim, FADE_ANIMATION_DURATION);
        }
    }

    private void creatSearchResultFragmetn() {
        Fragment fragment = ActivityUtil.findFragment(getSupportFragmentManager(), SearchResultFragmetnAdministrator.class.getName());
        if (fragment == null) {
            scrim.setVisibility(View.GONE);
            findViewById(R.id.searchBar_background).setVisibility(View.VISIBLE);
            ActivityUtil.replaceFragment(getSupportFragmentManager(), SearchResultFragmetnAdministrator.newInstance(), R.id.searchResult);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void showWords(List<SearchWord> words) {
        mAdapter.clearAddAll(words);
        if (mRecyclerView.getVisibility() == View.GONE) {
            line.setVisibility(View.VISIBLE);
            AnimationUtil.unfold(mRecyclerView);
        }
    }

    @Override
    public void showSimilarityWords(List<SearchWord> correlationWords) {
        mAdapter.clearAddAll(correlationWords);
        AnimationUtil.unfoldIncrease(mRecyclerView);
    }

    @Override
    public void showResultError(Throwable throwable) {

    }

    @Override
    public void showResultEmpty() {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getFragments().size() > 0) {
            if (searchEditText.isFocused()) {
                hindSearchState(false);
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Fragment fragment = ActivityUtil.findFragment(getSupportFragmentManager(), SearchResultFragmetnAdministrator.class.getName());
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment);
        }
        super.onDestroy();
    }
}
