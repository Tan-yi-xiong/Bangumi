package com.TyxApp.bangumi.main.search.SearchResult.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;

import androidx.annotation.NonNull;

public class SearchResultFragmentRVAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {

    public SearchResultFragmentRVAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_search_result);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        holder.setImageRes(R.id.cover, bangumi.getCover());
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.bangumi_ji_total, bangumi.getLatestJi());
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), bangumi));
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return getDataList().size();
    }

}
