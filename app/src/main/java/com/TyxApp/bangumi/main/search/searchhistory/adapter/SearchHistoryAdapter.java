package com.TyxApp.bangumi.main.search.searchhistory.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.SearchWord;

import androidx.annotation.NonNull;

public class SearchHistoryAdapter extends BaseAdapter<SearchWord, BaseViewHolder> {

    public SearchHistoryAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_search_history);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setText(R.id.tv_history_word, getDataList().get(position).getWord());
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickLisener != null) {
                return mOnItemLongClickLisener.onItemLongClick(position);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }
}
