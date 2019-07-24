package com.TyxApp.bangumi.main.myfavorite.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

import androidx.annotation.NonNull;

public class MyFavoriteAdpater extends BaseAdapter<Bangumi, BaseViewHolder> {

    public MyFavoriteAdpater(Context context) {
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
            } else {
                builder.append("更新至");
                builder.append(bangumi.getSerial());
            }
            builder.append("话");
            jiTotal = builder.toString();
        }
        holder.setText(R.id.bangumi_ji, jiTotal);
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(),bangumi));
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickLisener != null) {
               return mOnItemLongClickLisener.onItemLongClick(bangumi);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }



}
