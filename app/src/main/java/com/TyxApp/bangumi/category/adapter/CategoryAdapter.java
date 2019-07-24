package com.TyxApp.bangumi.category.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

import androidx.annotation.NonNull;

public class CategoryAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {

    public CategoryAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_category);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        int imageAngle = AnimationUtil.dp2px(getContext(), 3);
        Bangumi bangumi = getData(position);
        holder.setRoundedImage(R.id.bangumi_cover, bangumi.getCover(), imageAngle);
        holder.setText(R.id.bangumi_name, bangumi.getName());
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
