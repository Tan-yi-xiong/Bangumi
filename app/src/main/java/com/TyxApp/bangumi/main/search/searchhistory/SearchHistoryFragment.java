package com.TyxApp.bangumi.main.search.searchhistory;

import android.graphics.Color;
import android.os.Bundle;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.RecyclerViewFragment;
import com.TyxApp.bangumi.data.bean.SearchWord;
import com.TyxApp.bangumi.main.search.searchhistory.adapter.SearchHistoryAdapter;
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
    protected void initView(Bundle savedInstanceState) {
        getRefreshLayout().setEnabled(false);
        mSearchHistoryAdapter = new SearchHistoryAdapter(getActivity());
        getRecyclerview().setBackgroundColor(Color.WHITE);
        getRecyclerview().setAdapter(mSearchHistoryAdapter);
        getRecyclerview().setHasFixedSize(true);
        getRecyclerview().setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        mSearchInput = requireActivity().findViewById(R.id.search_input);
        mSearchInput.setOnTextChangeListener(text -> mSearchHistoryPresenter.getSimilarityWords(text));

        mSearchHistoryAdapter.setOnItemClickListener(position -> {
            String word = mSearchHistoryAdapter.getData(position).getWord();
            if (mOnSearchWordItemClickListener != null) {
                mOnSearchWordItemClickListener.OnSearchWordItemClick(word);
            }
        });

        mSearchHistoryAdapter.setOnItemLongClickLisener(position -> {
            mSearchHistoryPresenter.removeWord(mSearchHistoryAdapter.getData(position));
            mSearchHistoryAdapter.remove(position);
            return true;
        });
    }

    public void setOnSearchWordItemClickListener(OnSearchWordItemClickListener onSearchWordItemClickListener) {
        mOnSearchWordItemClickListener = onSearchWordItemClickListener;
    }

    @Override
    public BasePresenter getPresenter() {
        mSearchHistoryPresenter = new SearchHistoryPresenter(this);
        return mSearchHistoryPresenter;
    }

    @Override
    public void FristLoading() {
        mSearchHistoryPresenter.getWords();
    }

    @Override
    public void showWords(List<SearchWord> words) {
        mSearchHistoryAdapter.addAllInserted(words);
    }

    public static SearchHistoryFragment newInstance() {
        return new SearchHistoryFragment();
    }

    @Override
    public void showResultError(Throwable throwable) {

    }

    @Override
    public void showResultEmpty() {
        if (!mSearchHistoryAdapter.isEmpty()) {
            mSearchHistoryAdapter.clear();
            mSearchHistoryAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void showSimilarityWords(List<SearchWord> similarityWords) {
        mSearchHistoryAdapter.clearAddAll(similarityWords);
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
