package com.TyxApp.bangumi.player.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

public class RecommendAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {

    public RecommendAdapter(Activity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_player_recommend);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        holder.setRoundedImage(R.id.recommend_cover, bangumi.getCover(), AnimationUtil.dp2px(getContext(), 3));
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.ji, bangumi.getLatestJi());
        holder.itemView.setOnClickListener(v -> {
            PlayerActivity.startPlayerActivity(getContext(), bangumi);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }
}
