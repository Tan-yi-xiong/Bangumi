package com.TyxApp.bangumi.main.bangumi.adapter.dilidli;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.categoryresult.CategoryResultActivity;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.bangumi.adapter.zzzfun.ZzzFunHomeAdapter;
import com.TyxApp.bangumi.player.PlayerActivity;


public class DilidiliHomeAdapter extends ZzzFunHomeAdapter {

    public DilidiliHomeAdapter(Context context) {
        super(context);
        titles = context.getResources().getStringArray(R.array.dilidili_title);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_home_header);
        } else if (viewType == TITLE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.item_dilidili_title);
        } else  {
            return BaseViewHolder.get(getContext(), parent, R.layout.item_dilidili_home);
        }
    }

    @Override
    protected void bindBody(int position, BaseViewHolder holder) {
        int group = getBodyGroup(position);
        int index = 0;
        if (position % 2 == 1) {
            index = 2;
        } else if (2 * (group * 2 + 1) != position) {
            index = 4;
        }
        Bangumi leftBangumi = getData(group).get(index);
        holder.setImageRes(R.id.bangumi_cover_left, leftBangumi.getCover());
        holder.setText(R.id.bangumi_ji, leftBangumi.getLatestJi());
        holder.setText(R.id.bangumi_name_left, leftBangumi.getName());
        holder.getView(R.id.parent_left).setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), leftBangumi));


        Bangumi rightBangumi = getData(group).get(index + 1);
        holder.setText(R.id.bangumi_name_right, rightBangumi.getName());
        holder.setText(R.id.bangumi_ji_right, rightBangumi.getLatestJi());
        holder.setImageRes(R.id.bangumi_cover_right, rightBangumi.getCover());
        holder.getView(R.id.parent_right).setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), rightBangumi));
    }

    @Override
    protected void bindTitle(int position, BaseViewHolder holder) {
        position = (position - 1) / 4;
        holder.setText(R.id.title, titles[position]);
        int finalPosition = position;
        holder.getView(R.id.more_text).setOnClickListener(v -> CategoryResultActivity.startCategoryResultActivity(getContext(), titles[finalPosition]));
    }
}
