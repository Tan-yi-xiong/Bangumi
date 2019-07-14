package com.TyxApp.bangumi.mainpage.search.searchhistory;

import android.graphics.Color;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.mainpage.search.searchhistory.adapter.SearchHistoryAdapter;
import com.TyxApp.bangumi.view.SearchInput;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchHistoryFragment extends RecyclerViewFragment implements SearchHistoryContract.View {
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchHistoryPresenter mSearchHistoryPresenter;
    private SearchInput mSearchInput;
    private OnSearchWordItemClickListener mOnSearchWordItemClickListener;

    @Override
    protected void initView() {
        getRefreshLayout().setEnabled(false);
        mSearchHistoryAdapter = new SearchHistoryAdapter(getActivity());
        getRecyclerview().setBackgroundColor(Color.WHITE);
        getRecyclerview().setAdapter(mSearchHistoryAdapter);
        getRecyclerview().setHasFixedSize(true);
        getRecyclerview().setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        mSearchInput = requireActivity().findViewById(R.id.search_input);
        mSearchInput.setOnTextChangeListener(text -> mSearchHistoryPresenter.getCorrelationWords(text));

        if (mOnSearchWordItemClickListener != null) {
            mSearchHistoryAdapter.setOnItemClickListener(pos -> {
                String word = mSearchHistoryAdapter.getData(pos);
                if (mOnSearchWordItemClickListener != null) {
                    mOnSearchWordItemClickListener.OnSearchWordItemClick(word);
                }
            });
        }

        mSearchHistoryPresenter.getWords();
    }

    public void setOnSearchWordItemClickListener(OnSearchWordItemClickListener onSearchWordItemClickListener) {
        if (mOnSearchWordItemClickListener != null) {
            return;
        }
        mOnSearchWordItemClickListener = onSearchWordItemClickListener;
    }

    @Override
    public BasePresenter getPresenter() {
        mSearchHistoryPresenter = new SearchHistoryPresenter(this);
        return mSearchHistoryPresenter;
    }

    @Override
    public void showWords(List<String> words) {
        mSearchHistoryAdapter.addAllInserted(words);
    }

    public static SearchHistoryFragment newInstance() {
        return new SearchHistoryFragment();
    }

    @Override
    public void showWordsEmpty() {
        if (!mSearchHistoryAdapter.isEmpty()) {
            mSearchHistoryAdapter.clear();
            mSearchHistoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showCorrelationWords(List<String> correlationWords) {
        mSearchHistoryAdapter.clearAddAll(correlationWords);
    }

    @Override
    public void onDestroyView() {
        mSearchInput.removeOnTextChangeListener();
        super.onDestroyView();
    }

    public interface OnSearchWordItemClickListener {
        void OnSearchWordItemClick(String word);
    }
}
