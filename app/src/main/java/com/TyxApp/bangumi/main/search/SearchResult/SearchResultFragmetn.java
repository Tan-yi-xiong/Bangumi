package com.TyxApp.bangumi.main.search.SearchResult;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.IBangumiParser;
import com.TyxApp.bangumi.data.source.remote.Nico;
import com.TyxApp.bangumi.data.source.remote.Sakura;
import com.TyxApp.bangumi.data.source.remote.Silisili;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.main.search.SearchResult.adapter.SearchResultRVAdapter;
import com.TyxApp.bangumi.util.ExceptionUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SearchResultFragmetn extends RecyclerViewFragment implements SearchResultContract.View {
    public static final String ARG_KEY = "P_TYPE";
    private String lastSearchWord;
    private EditText mEditText;
    private SearchResultPresenter mPresenter;
    private SearchResultRVAdapter mAdapter;
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
    protected void initView(View view, Bundle savedInstanceState) {
        getRefreshLayout().setEnabled(false);
        SearchView searchView = requireActivity().findViewById(R.id.searchView);
        int id = searchView.getResources().getIdentifier("android:id/search_src_text", null, null);
        mEditText = searchView.findViewById(id);
        mAdapter = new SearchResultRVAdapter(requireActivity());
        getRecyclerview().setAdapter(mAdapter);

        getErrorPageView().setOnClickListener(v -> {
            showDataLodaing();
            mPresenter.getSearchResult(mEditText.getText().toString());
        });
    }

    @Override
    public BasePresenter getPresenter() {
        ExceptionUtil.checkNull(getArguments(), "必须通过newInstance方法创建实例");
        String type = getArguments().getString(ARG_KEY);
        IBangumiParser parser = null;
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

            case BangumiPresistenceContract
                    .BangumiSource.SILISILI:
                parser = Silisili.newInstance();
            break;
        }
        mPresenter = new SearchResultPresenter(parser, this);
        return mPresenter;
    }


    @Override
    public void onResume() {
        super.onResume();
        String qureWord = mEditText.getText().toString();
        if (TextUtils.isEmpty(qureWord)) {
            Toast.makeText(requireContext(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!qureWord.equals(lastSearchWord)) {
            lastSearchWord = qureWord;
            showDataLodaing();
            mPresenter.getSearchResult(lastSearchWord);
        }
    }


    @Override
    public void showSearchResult(List<Bangumi> bangumis) {
        showRecyclerView();
        isLoading = false;
        getRecyclerview().scrollToPosition(0);
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
