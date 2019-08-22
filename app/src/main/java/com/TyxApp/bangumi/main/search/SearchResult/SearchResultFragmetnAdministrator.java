package com.TyxApp.bangumi.main.search.SearchResult;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.main.search.SearchResult.adapter.SearchResultVPAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchResultFragmetnAdministrator extends Fragment {
    @BindView(R.id.search_result_tab)
    TabLayout searchResultTabLayout;
    @BindView(R.id.vp_search_result)
    ViewPager searchResultViewPager;

    private List<SearchResultFragment> searchResultFragmetns;
    private Unbinder mUnbinder;
    private String[] tabTexts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        tabTexts = requireActivity().getResources().getStringArray(R.array.search_result_tab_text);
        view.setBackgroundColor(Color.WHITE);
        initView();
        return view;
    }

    private void initView() {
        searchResultFragmetns = new ArrayList<>();
        for (String tabText : tabTexts) {
            SearchResultFragment searchResultFragmetn = SearchResultFragment.newInstance(tabText);
            searchResultFragmetns.add(searchResultFragmetn);
        }

        SearchResultVPAdapter adapter = new SearchResultVPAdapter(
                getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                Arrays.asList(tabTexts),
                searchResultFragmetns);

        searchResultViewPager.setAdapter(adapter);
        searchResultViewPager.setOffscreenPageLimit(tabTexts.length);
        searchResultTabLayout.setupWithViewPager(searchResultViewPager);
    }

    @Override
    public void onResume() {
        setCurrentSelectFragmentState(Lifecycle.State.RESUMED);
        super.onResume();
    }

    private void setCurrentSelectFragmentState(Lifecycle.State state) {
        int currentSelectFragment = searchResultTabLayout.getSelectedTabPosition();
        SearchResultFragment fragmetn = searchResultFragmetns.get(currentSelectFragment);
        if (!fragmetn.isAdded()) {
            return;
        }
        getChildFragmentManager().beginTransaction()
                .setMaxLifecycle(fragmetn, state)
                .commit();
    }

    @Override
    public void onPause() {
        setCurrentSelectFragmentState(Lifecycle.State.STARTED);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        searchResultFragmetns.clear();
        super.onDestroyView();
    }


    public static SearchResultFragmetnAdministrator newInstance() {
        return new SearchResultFragmetnAdministrator();
    }

}
