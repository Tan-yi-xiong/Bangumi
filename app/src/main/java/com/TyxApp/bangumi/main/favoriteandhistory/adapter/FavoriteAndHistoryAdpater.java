package com.TyxApp.bangumi.main.favoriteandhistory.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;

public class FavoriteAndHistoryAdpater extends BaseAdapter<Bangumi, BaseViewHolder> {
    private boolean showTime;
    private SimpleDateFormat mDateFormat;

    public FavoriteAndHistoryAdpater(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_favorite);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        int imageAngle = AnimationUtil.dp2px(getContext(), 3);
        holder.setRoundedImage(R.id.cover, bangumi.getCover(), imageAngle);
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.bangumi_source, bangumi.getVideoSoure());
        holder.setText(R.id.bangumi_ji, bangumi.getLatestJi());
        if (showTime) {
            holder.setText(R.id.history_time, mDateFormat.format(bangumi.getHistoryTime()));
        }
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), bangumi));
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

    public void setShowTime(boolean showTime) {
        if (showTime) {
            if (mDateFormat == null) {
                mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
        }
        this.showTime = showTime;
    }
}
