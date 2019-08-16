package com.TyxApp.bangumi.player.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.util.AnimationUtil;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class JiAdapter extends BaseAdapter<TextItemSelectBean, BaseViewHolder> {
    private RecyclerView mRecyclerView;

    public JiAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_player_ji);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        TextItemSelectBean selectBean = getData(position);
        holder.setText(R.id.ji, selectBean.getText());
        if (selectBean.isSelect()) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
        holder.itemView.setOnClickListener(v -> {
            jiItemSelect(position);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    public void jiItemSelect(int position) {
        int lastSelectPosition = -1;
        for (int i = 0; i < getDataList().size(); i++) {
            if (getData(i).isSelect()) {
                lastSelectPosition = i;
                break;
            }
        }
        if (mRecyclerView != null && lastSelectPosition != -1 && lastSelectPosition != position) {
            RecyclerView.ViewHolder selectHolder = mRecyclerView.findViewHolderForLayoutPosition(position);
            RecyclerView.ViewHolder lastSelectHolder = mRecyclerView.findViewHolderForLayoutPosition(lastSelectPosition);
            if (selectHolder != null) {
                selectHolder.itemView.setSelected(true);
            }
            if (lastSelectHolder != null) {
                lastSelectHolder.itemView.setSelected(false);
            } else {
                notifyItemChanged(lastSelectPosition);
            }
            getData(lastSelectPosition).setSelect(false);
            getData(position).setSelect(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new ItemDecoration());
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    public static class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int lastPosition = parent.getAdapter().getItemCount() - 1;
            if (position == lastPosition) {
                outRect.right = AnimationUtil.dp2px(view.getContext(), 12);
            }
            outRect.left = AnimationUtil.dp2px(view.getContext(), 12);
        }
    }
}
