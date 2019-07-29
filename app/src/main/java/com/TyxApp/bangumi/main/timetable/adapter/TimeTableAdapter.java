package com.TyxApp.bangumi.main.timetable.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeTableAdapter extends BaseAdapter<List<Bangumi>, BaseViewHolder> {
    private int mWeek;

    public TimeTableAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_favorite);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(mWeek).get(position);
        int imageAngle = AnimationUtil.dp2px(getContext(), 3);
        holder.setRoundedImage(R.id.cover, bangumi.getCover(), imageAngle);
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.bangumi_ji, bangumi.getSerial());
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), bangumi));
    }

    public void setWeek(int week) {
        if (mWeek == week) {
            return;
        }
        notifyItemRangeRemoved(0, getData(mWeek).size());
        mWeek = week;
        notifyItemRangeInserted(0, getData(mWeek).size());
    }

    @Override
    public int getItemCount() {
        return getDataList().size() == 0 ? 0 : getData(mWeek).size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        super.onAttachedToRecyclerView(recyclerView);
    }
}
