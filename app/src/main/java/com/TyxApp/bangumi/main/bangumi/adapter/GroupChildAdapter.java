package com.TyxApp.bangumi.main.bangumi.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

public class GroupChildAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {
    private SparseIntArray mSparseIntArray;//记录展开显示的条目, 防止回收机制导致内容错位

    public GroupChildAdapter(Context context) {
        super(context);
        mSparseIntArray = new SparseIntArray();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_group_child);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        int tag = mSparseIntArray.get(position, -1);
        if (tag == -1 && holder.getView(R.id.name_left).getVisibility() == View.VISIBLE) {
            change(holder, bangumi, false, false);
        } else if (tag != -1 && holder.getView(R.id.name_left).getVisibility() == View.GONE){
            change(holder, bangumi, true, false);
        }
        holder.setImageRes(R.id.cover, bangumi.getCover());
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.ji, bangumi.getLatestJi());
        holder.itemView.setOnClickListener(v -> {
            if (holder.getView(R.id.name_left).getVisibility() == View.VISIBLE) {
                change(holder, bangumi, false, true);
                mSparseIntArray.delete(position);
                return;
            }
            PlayerActivity.startPlayerActivity(getContext(), bangumi);
        });
        holder.itemView.setOnLongClickListener(v -> {
            mSparseIntArray.append(position, position);
            change(holder, bangumi, true, true);
            return true;
        });
    }

    public void change(BaseViewHolder holder, Bangumi bangumi, boolean isShow, boolean isTransition) {
        if (isTransition) {
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView, new ChangeBounds());
        }
        TextView name = holder.getView(R.id.name);
        TextView nameLeft = holder.getView(R.id.name_left);
        TextView intro = holder.getView(R.id.intro);
        TextView type = holder.getView(R.id.type);
        if (isShow) {
            intro.setText(bangumi.getIntro());
            type.setText(bangumi.getType());
            nameLeft.setText(bangumi.getName());
        }
        intro.setVisibility(isShow ? View.VISIBLE : View.GONE);
        nameLeft.setVisibility(isShow ? View.VISIBLE : View.GONE);
        type.setVisibility(isShow ? View.VISIBLE : View.GONE);
        name.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
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
            outRect.right = AnimationUtil.dp2px(getContext(), 16);
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = AnimationUtil.dp2px(getContext(), 8);
            }
        }
    }
}
