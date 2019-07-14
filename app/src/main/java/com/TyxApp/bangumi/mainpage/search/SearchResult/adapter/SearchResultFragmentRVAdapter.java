package com.TyxApp.bangumi.mainpage.search.SearchResult.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.util.AnimationUtil;

import androidx.annotation.NonNull;

public class SearchResultFragmentRVAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {
    private static final int LOADING_VIEW_TYPE = 1;
    private static final int DATA_VIEW_TYPE = 0;

    public SearchResultFragmentRVAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_VIEW_TYPE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.item_search_result);
        } else {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_bottom_loading);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder.getItemViewType() != LOADING_VIEW_TYPE) {
            Bangumi bangumi = getData(position);
            holder.setImageRes(R.id.result_cover, bangumi.getCover());
            holder.setText(R.id.result_name, bangumi.getName());
            String jiTotal = bangumi.getRemarks();
            if (TextUtils.isEmpty(jiTotal)) {
                StringBuilder builder = new StringBuilder();
                if (!TextUtils.isEmpty(bangumi.getTotal())) {
                    if (Integer.valueOf(bangumi.getTotal()) == 0){
                        builder.append("更新至");
                        builder.append(bangumi.getSerial());
                    } else {
                        builder.append("全");
                        builder.append(bangumi.getTotal());
                    }
                }
                builder.append("集");
                jiTotal = builder.toString();
            }
            holder.setText(R.id.result_ji_toatal, jiTotal);
            holder.itemView.setOnClickListener( v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        //最后一条为null证明还可以加载, 显示加载底部.
        if (position == getDataList().size() - 1 && getData(position) == null) {
            return LOADING_VIEW_TYPE;
        }
        return DATA_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

}
