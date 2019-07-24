package com.TyxApp.bangumi.main.search.SearchResult;


import android.os.Bundle;
import android.widget.Toast;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.Nico;
import com.TyxApp.bangumi.data.source.remote.Sakura;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.main.search.SearchResult.adapter.SearchResultFragmentRVAdapter;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.TyxApp.bangumi.view.SearchInput;
import com.google.android.material.snackbar.Snackbar;

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
    private boolean isLoading;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (!recyclerView.canScrollVertically(1)) {
                if (isLoading) return;
                isLoading = true;
                mPresenter.getMoreSearchResult();
            }
        }
    };

    @Override
    protected void initView() {
        getRefreshLayout().setEnabled(false);
        mSearchInput = requireActivity().findViewById(R.id.search_input);
        mAdapter = new SearchResultFragmentRVAdapter(requireActivity());
        getRecyclerview().setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        getRecyclerview().setAdapter(mAdapter);

        getErrorPageView().setOnClickListener(v -> mPresenter.getSearchResult(lastSearchWord));
    }

    @Override
    public BasePresenter getPresenter() {
        ExceptionUtil.checkNull(getArguments(), "必须通过newInstance方法创建实例");
        String type = getArguments().getString(ARG_KEY);
        BaseBangumiParser parser = null;
        switch (type) {
            case BangumiPresistenceContract
                    .BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;

            case BangumiPresistenceContract
                    .BangumiSource.NiICO:
                parser = Nico.getInstance();
                break;

            case BangumiPresistenceContract
                    .BangumiSource.SAKURA:
                parser = Sakura.getInstance();
                break;

            case BangumiPresistenceContract
                    .BangumiSource.DILIDLI:
                parser = Dilidili.getInstance();
            break;
        }
        mPresenter = new SearchResultPresenter(parser, this);
        return mPresenter;
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
        showRecyclerView();
        isLoading = false;
        getRecyclerview().clearOnScrollListeners();
        getRecyclerview().addOnScrollListener(mScrollListener);
        mAdapter.clearAddAll(bangumis);
    }

    @Override
    public void showMoreSearchResult(List<Bangumi> moreBangumis) {
        isLoading = false;
        mAdapter.addAllInserted(moreBangumis);
    }

    @Override
    public void noNextSearchResult() {
        mAdapter.remove(mAdapter.getDataList().size());
        Toast.makeText(getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
        getRecyclerview().removeOnScrollListener(mScrollListener);
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

    @Override
    public void showResultError(Throwable throwable) {
        showErrorPage();
        Snackbar.make(getRecyclerview(), "发生错误", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showResultEmpty() {
        showNoResult();
    }
}
