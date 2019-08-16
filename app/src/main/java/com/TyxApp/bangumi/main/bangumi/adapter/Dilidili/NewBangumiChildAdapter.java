package com.TyxApp.bangumi.main.bangumi.adapter.Dilidili;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
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

public class NewBangumiChildAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {
    public NewBangumiChildAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_dilidili_newbangumi);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        holder.setRoundedImage(R.id.cover, bangumi.getCover(), AnimationUtil.dp2px(getContext(), 4));
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.ji, bangumi.getLatestJi());
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), bangumi));
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new ItemDecoration());
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int distance = AnimationUtil.dp2px(getContext(), 8);
            outRect.left = distance;
            int postion = parent.getChildAdapterPosition(view);
            if (postion == getItemCount() - 1) {
                outRect.right = distance;
            }
        }
    }
}
