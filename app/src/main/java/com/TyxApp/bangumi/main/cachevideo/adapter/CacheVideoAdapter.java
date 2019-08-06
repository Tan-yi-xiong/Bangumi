package com.TyxApp.bangumi.main.cachevideo.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.downloaddetails.DownloadDetailsActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

public class CacheVideoAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {
    public CacheVideoAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_grid_category);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        holder.setRoundedImage(R.id.cover, bangumi.getCover(), AnimationUtil.dp2px(getContext(), 3));
        holder.setText(R.id.name, bangumi.getName());
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickLisener != null) {
                return mOnItemLongClickLisener.onItemLongClick(position);
            }
            return false;
        });
        holder.itemView.setOnClickListener(v -> DownloadDetailsActivity.startDownloadDetailsActivity(getContext(), bangumi.getVodId(), bangumi.getVideoSoure()));
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        super.onAttachedToRecyclerView(recyclerView);
    }
}
