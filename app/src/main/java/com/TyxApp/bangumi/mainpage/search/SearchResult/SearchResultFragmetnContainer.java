package com.TyxApp.bangumi.mainpage.search.SearchResult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchResultFragmetnContainer extends Fragment {
    @BindView(R.id.search_result_tab)
    TabLayout searchResultTabLayout;
    @BindView(R.id.vp_search_result)
    ViewPager searchResultViewPager;

    private Unbinder mUnbinder;
    private String[] tabTexts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        tabTexts = getContext().getResources().getStringArray(R.array.search_result_tab_text);
        return view;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    public void search(String word) {

    }

    public static SearchResultFragmetnContainer newInstance() {
       return new SearchResultFragmetnContainer();
    }
}
