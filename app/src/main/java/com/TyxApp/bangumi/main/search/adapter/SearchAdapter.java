package com.TyxApp.bangumi.main.search.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.SearchWord;

public class SearchAdapter extends BaseAdapter<SearchWord, BaseViewHolder> {

    public SearchAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_search_history);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        SearchWord searchWord = getDataList().get(position);
        ImageView imageView = holder.getView(R.id.hint_imageView);
        if (searchWord.isFromNet()) {
            imageView.setImageResource(R.drawable.ic_search);
        } else {
            imageView.setImageResource(R.drawable.historical);
        }
        holder.setText(R.id.tv_history_word, searchWord.getWord());
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickLisener != null) {
                return mOnItemLongClickLisener.onItemLongClick(holder.getAdapterPosition());
            }
            return false;
        });
    }

    @Override
    public void remove(int position) {
        getDataList().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }
}
