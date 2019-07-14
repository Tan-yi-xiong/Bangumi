package com.TyxApp.bangumi.mainpage.search.SearchResult.adapter;

import com.TyxApp.bangumi.mainpage.search.SearchResult.SearchResultFragmetn;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SearchResultFragmentPageAdapter extends FragmentPagerAdapter {
    private List<SearchResultFragmetn> mSearchResultFragmetns;
    private List<String> tabTexts;

    public SearchResultFragmentPageAdapter(@NonNull FragmentManager fm, int behavior,
                                           List<String> tabTexts,
                                           List<SearchResultFragmetn> searchResultFragmetns) {

        super(fm, behavior);
        this.tabTexts = tabTexts;
        mSearchResultFragmetns = searchResultFragmetns;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mSearchResultFragmetns.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTexts.get(position);
    }

    @Override
    public int getCount() {
        return mSearchResultFragmetns == null ? 0 : mSearchResultFragmetns.size();
    }
}
