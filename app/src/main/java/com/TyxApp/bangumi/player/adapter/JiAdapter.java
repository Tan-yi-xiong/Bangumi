package com.TyxApp.bangumi.player.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.util.AnimationUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JiAdapter extends BaseAdapter<TextItemSelectBean, BaseViewHolder> {
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
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
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
