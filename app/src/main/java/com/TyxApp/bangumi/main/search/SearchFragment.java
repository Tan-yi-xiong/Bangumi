package com.TyxApp.bangumi.main.search;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.main.search.SearchResult.SearchResultFragmetnContainer;
import com.TyxApp.bangumi.main.search.searchhistory.SearchHistoryFragment;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.view.SearchInput;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

public class SearchFragment extends BaseMvpFragment {
    private SearchInput mSearchInput;
    private static final String SHF_NAME = "SearchHistoryFragmentName";
    private SearchPresenter mPresenter;
    private String lastInPutWord;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //处理返回按键事件
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getChildFragmentManager().getBackStackEntryCount() != 0) {
                    getChildFragmentManager().popBackStack();
                    if (!mSearchInput.getText().equals(lastInPutWord)) {
                        mSearchInput.setText(lastInPutWord);
                    }
                    mSearchInput.clearFocus();
                } else {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                this,
                backPressedCallback);

        getChildFragmentManager().addOnBackStackChangedListener(() -> {
            if (getChildFragmentManager().getBackStackEntryCount() == 0) {
                setSearchResultFragmetLifecycle(Lifecycle.State.RESUMED);
            } else if (getChildFragmentManager().getBackStackEntryCount() == 1) {
                setSearchResultFragmetLifecycle(Lifecycle.State.STARTED);
            }
        });
    }

    private void setSearchResultFragmetLifecycle(Lifecycle.State state) {
        SearchResultFragmetnContainer resultFragmetnContainer = (SearchResultFragmetnContainer) ActivityUtil.findFragment(
                getChildFragmentManager(), SearchResultFragmetnContainer.class.getName());

        getChildFragmentManager().beginTransaction()
                .setMaxLifecycle(resultFragmetnContainer, state)
                .commit();
    }

    public void initView(Bundle savedInstanceState) {
        mSearchInput = requireActivity().findViewById(R.id.search_input);
        mSearchInput.setVisibility(View.VISIBLE);

        mSearchInput.setOnFocusChangeListener((SearchInput.OnFocusChangeListener) (view, hasFocus) -> {
            if (hasFocus) {
                SearchResultFragmetnContainer resultFragmetnContainer = (SearchResultFragmetnContainer) ActivityUtil.findFragment(
                        getChildFragmentManager(), SearchResultFragmetnContainer.class.getName());
                //搜索历史页面不是在最底部, 就是搜索结果页面在底部, 如输入框再次获取焦点搜索历史界面应该再次显示出来
                if (resultFragmetnContainer != null) {
                    getChildFragmentManager().beginTransaction()
                            .setMaxLifecycle(resultFragmetnContainer, Lifecycle.State.STARTED)
                            .commit();

                    ActivityUtil.addFragmentToBackTask(getChildFragmentManager(),
                            getSearchHistoryFragmentInstance(),
                            R.id.fl_search_content, SHF_NAME);
                }
            }
        });

        mSearchInput.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String word = v.getText().toString();
                if (!TextUtils.isEmpty(word)) {
                    mSearchInput.editTextClearFocus();
                    mPresenter.saveWord(new SearchWord(System.currentTimeMillis(), word));
                    replaceOrSearch();
                    return false;
                }
            }
            return true;
        });

        ActivityUtil.replaceFragment(
                getChildFragmentManager(),
                getSearchHistoryFragmentInstance(),
                R.id.fl_search_content);
    }

    private SearchHistoryFragment getSearchHistoryFragmentInstance() {
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        searchHistoryFragment.setOnSearchWordItemClickListener(word -> {
            mSearchInput.setText(word);
            mSearchInput.clearFocus();
            mPresenter.saveWord(new SearchWord(System.currentTimeMillis(), word));
            replaceOrSearch();
        });
        return searchHistoryFragment;
    }

    private void replaceOrSearch() {
        SearchResultFragmetnContainer resultFragmetnContainer = (SearchResultFragmetnContainer) ActivityUtil.findFragment(
                getChildFragmentManager(), SearchResultFragmetnContainer.class.getName());
        lastInPutWord = mSearchInput.getText();
        if (resultFragmetnContainer == null) {
            ActivityUtil.replaceFragment(getChildFragmentManager(),
                    SearchResultFragmetnContainer.newInstance(), R.id.fl_search_content);
        } else {
            getChildFragmentManager().popBackStack();
        }
    }

    @Override
    public BasePresenter getPresenter() {
        mPresenter = new SearchPresenter();
        return mPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onDestroyView() {
        mSearchInput.setText(null);
        mSearchInput.removeOnFocusChangeListener();
        mSearchInput.setVisibility(View.GONE);
        super.onDestroyView();
    }
}
