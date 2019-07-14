package com.TyxApp.bangumi.mainpage.search.SearchResult;


import android.os.Bundle;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.Nico;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.mainpage.search.SearchResult.adapter.SearchResultFragmentRVAdapter;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.view.SearchInput;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultFragmetn extends RecyclerViewFragment implements SearchResultContract.View {
    public static final String ARG_KEY = "P_TYPE";
    private SearchInput mSearchInput;
    private String lastSearchWord;
    private SearchResultPresenter mPresenter;
    private SearchResultFragmentRVAdapter mAdapter;
    private boolean isLoding;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (!recyclerView.canScrollVertically(1)) {
                if (isLoding) return;
                isLoding = true;
                mPresenter.getMoreSearchResult();
            }
        }
    };

    @Override
    protected void initView() {
        getRefreshLayout().setEnabled(false);
        mSearchInput = requireActivity().findViewById(R.id.search_input);
        mAdapter = new SearchResultFragmentRVAdapter(requireContext());
        getRecyclerview().setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        getRecyclerview().setAdapter(mAdapter);
    }

    @Override
    public BasePresenter getPresenter() {
        ExceptionUtil.checkNull(getArguments(), "必须通过newInstance方法创建实例");
        String type = getArguments().getString(ARG_KEY);
        BaseBangumiParser parser = null;
        switch (type) {
            case BangumiPresistenceContract
                    .BangumiSource.ZZZFUN:
                parser = new ZzzFun();
                break;
            case BangumiPresistenceContract
                    .BangumiSource.NiICO:
                parser = new Nico();
                break;

            default:
                parser = new ZzzFun();
        }
        mPresenter = new SearchResultPresenter(parser, this);
        return mPresenter;
    }

    @Override
    public void showSearchResultLoadingError(Throwable throwable) {
        LogUtil.i(throwable.toString());
    }

    @Override
    public void onResume() {
        if (!mSearchInput.getText().equals(lastSearchWord)) {
            lastSearchWord = mSearchInput.getText();
            showDataLodaing();
            mPresenter.getSearchResult(lastSearchWord);
        }
        super.onResume();
    }


    @Override
    public void showSearchResult(List<Bangumi> bangumis) {
        if (bangumis.get(bangumis.size() - 1) == null) {
            getRecyclerview().addOnScrollListener(mScrollListener);
        }
        showRecyclerView();
        mAdapter.clearAddAll(bangumis);
    }

    @Override
    public void showMoreSearchResult(List<Bangumi> moreBangumis) {
        isLoding = false;
        if (moreBangumis.get(moreBangumis.size() - 1) != null) {
            getRecyclerview().removeOnScrollListener(mScrollListener);
        }
        mAdapter.remove(mAdapter.getItemCount() - 1);//移除加载状态
        mAdapter.addAllInserted(moreBangumis);
    }

    @Override
    public void showSearchResultEmpty() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static SearchResultFragmetn newInstance(String presenterType) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, presenterType);
        SearchResultFragmetn fragment = new SearchResultFragmetn();
        fragment.setArguments(args);
        return fragment;
    }
}
