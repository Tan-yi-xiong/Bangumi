package com.TyxApp.bangumi.main.search.SearchResult.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.TyxApp.bangumi.main.search.SearchResult.SearchResultFragment;

import java.util.List;

public class SearchResultVPAdapter extends FragmentPagerAdapter {
    private List<SearchResultFragment> mSearchResultFragmetns;
    private List<String> tabTexts;

    public SearchResultVPAdapter(@NonNull FragmentManager fm, int behavior,
                                 List<String> tabTexts,
                                 List<SearchResultFragment> searchResultFragmetns) {

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
