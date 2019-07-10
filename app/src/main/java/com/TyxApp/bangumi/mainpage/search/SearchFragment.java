package com.TyxApp.bangumi.mainpage.search;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.mainpage.search.SearchResult.SearchResultFragmetnContainer;
import com.TyxApp.bangumi.mainpage.search.searchhistory.SearchHistoryFragment;
import com.TyxApp.bangumi.util.ActivityUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.view.SearchInput;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchFragment extends BaseFragment {
    private SearchInput mSearchInput;
    private static final String SHF_NAME = "SearchHistoryFragmentName";
    private SearchPresenter mPresenter;


    public void initView() {
        mSearchInput = getActivity().findViewById(R.id.search_input);
        mSearchInput.setOnFocusChangeListener((SearchInput.OnFocusChangeListener) (view, hasFocus) -> {
            if (hasFocus) {
                Fragment fragment = ActivityUtil.findFragment(getChildFragmentManager(),
                        SearchHistoryFragment.class.getName());

                //搜索历史页面不是在最底部, 就是搜索结果页面在底部, 如输入框再次获取焦点搜索历史界面应该再次显示出来, 否则替换为搜索结果
                if (fragment == null) {
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
                    mPresenter.saveWord(word);
                    replaceOrSearch(word);
                    return false;
                }
            }
            return true;
        });

        ActivityUtil.replaceFragment(getChildFragmentManager(), getSearchHistoryFragmentInstance(), R.id.fl_search_content);
    }

    private SearchHistoryFragment getSearchHistoryFragmentInstance() {
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        searchHistoryFragment.setOnSearchWordItemClickListener(word -> {
            mSearchInput.setText(word);
            mSearchInput.clearFocus();
            replaceOrSearch(word);
        });
        return searchHistoryFragment;
    }

    private void replaceOrSearch(String word) {
        SearchResultFragmetnContainer resultFragmetnContainer = (SearchResultFragmetnContainer) ActivityUtil.findFragment(
                getChildFragmentManager(), SearchResultFragmetnContainer.class.getName());

        if (resultFragmetnContainer == null) {
            ActivityUtil.replaceFragment(getChildFragmentManager(),
                    SearchResultFragmetnContainer.newInstance(), R.id.fl_search_content);
        } else {
            getChildFragmentManager().popBackStack();
            resultFragmetnContainer.search(word);
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

    @Override
    public void onResume() {
        mSearchInput.setVisibility(View.VISIBLE);
        super.onResume();
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public boolean hasChildPop() {
        return getChildFragmentManager().getBackStackEntryCount() != 0;
    }

    public void childPop() {
        getChildFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        mSearchInput.setText(null);
        mSearchInput.removeOnFocusChangeListener();
        mSearchInput.setVisibility(View.GONE);
        super.onDestroyView();
    }
}
