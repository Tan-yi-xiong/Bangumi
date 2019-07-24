package com.TyxApp.bangumi.main.history.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.myfavorite.adapter.MyFavoriteAdpater;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;

public class HistoryAdapter extends MyFavoriteAdpater {
    private SimpleDateFormat mSimpleDateFormat;

    public HistoryAdapter(Context context) {
        super(context);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        int imageAngle = AnimationUtil.dp2px(getContext(), 3);
        holder.setRoundedImage(R.id.bangumi_cover, bangumi.getCover(), imageAngle);
        holder.setText(R.id.bangumi_name, bangumi.getName());
        holder.setText(R.id.bangumi_source, bangumi.getVideoSoure());
        String jiTotal = bangumi.getRemarks();
        if (TextUtils.isEmpty(jiTotal)) {
            StringBuilder builder = new StringBuilder();
            jiTotal = bangumi.getTotal();
            if (!TextUtils.isEmpty(jiTotal) && !"0".equals(jiTotal)) {
                builder.append("全");
                builder.append(jiTotal);
                builder.append("话");
            } else if (!TextUtils.isEmpty(bangumi.getSerial())){
                builder.append("更新至");
                builder.append(bangumi.getSerial());
                bangumi.getSerial();
                builder.append("话");
            }
            jiTotal = builder.toString();
        }
        holder.setText(R.id.bangumi_ji, jiTotal);
        holder.setText(R.id.history_time, mSimpleDateFormat.format(bangumi.getTime()));
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(),bangumi));
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickLisener != null) {
                return mOnItemLongClickLisener.onItemLongClick(bangumi);
            }
            return false;
        });
    }
}
