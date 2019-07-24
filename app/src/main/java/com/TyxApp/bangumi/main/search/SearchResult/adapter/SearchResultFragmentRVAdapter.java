package com.TyxApp.bangumi.main.search.SearchResult.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.PlayerActivity;

import java.util.Collection;

import androidx.annotation.NonNull;

public class SearchResultFragmentRVAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {
    private static final int LOADING_VIEW_TYPE = 1;
    private static final int DATA_VIEW_TYPE = 0;
    private boolean showLoadingView;

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
        if (holder.getItemViewType() == LOADING_VIEW_TYPE) {
            return;
        }
        Bangumi bangumi = getData(position);
        holder.setImageRes(R.id.bangumi_cover, bangumi.getCover());
        holder.setText(R.id.bangumi_name, bangumi.getName());
        String jiTotal = bangumi.getRemarks();
        if (TextUtils.isEmpty(jiTotal)) {
            StringBuilder builder = new StringBuilder();
            if (!TextUtils.isEmpty(bangumi.getTotal())) {
                if (Integer.valueOf(bangumi.getTotal()) == 0) {
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
        holder.setText(R.id.bangumi_ji_total, jiTotal);
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), bangumi));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getDataList().size()) {
            return LOADING_VIEW_TYPE;
        }
        return DATA_VIEW_TYPE;
    }

    @Override
    public void clearAddAll(Collection<Bangumi> collection) {
        showLoadingView = true;
        super.clearAddAll(collection);
    }

    @Override
    public void remove(int position) {
        if (position == getDataList().size()) {
            showLoadingView = false;
            notifyItemRemoved(position);
            return;
        }
        super.remove(position);
    }

    @Override
    public int getItemCount() {
        return getDataList().isEmpty() ? 0 : showLoadingView ? getDataList().size() + 1 : getDataList().size();
    }

}
