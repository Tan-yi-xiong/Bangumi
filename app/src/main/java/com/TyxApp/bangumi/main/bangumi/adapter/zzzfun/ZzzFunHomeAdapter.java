package com.TyxApp.bangumi.main.bangumi.adapter.zzzfun;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.categoryresult.CategoryResultActivity;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.bangumi.adapter.BannerHomeAdapter;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ZzzFunHomeAdapter extends BannerHomeAdapter<BaseViewHolder> {
    private ItemDecoration mItemDecoration;
    protected String[] titles;

    public ZzzFunHomeAdapter(Context context) {
        super(context);
        titles = context.getResources().getStringArray(R.array.zzfun_title);
    }

    @Override
    public void populaterBangumis(List<List<Bangumi>> bangumis) {
        super.populaterBangumis(bangumis);
        getDataList().addAll(bangumis);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_home_header);
        } else if (viewType == TITLE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_zzzfun_title);
        } else if (viewType == BODY) {
            return BaseViewHolder.get(getContext(), parent, R.layout.item_zzzfun_home_bangumi);
        }
        return null;
    }

    protected void bindBody(int position, BaseViewHolder holder) {
        int group = getBodyGroup(position);
        int index = 0;
        if (position % 2 == 1) {
            index = 2;
        } else if (2 * (group * 2 + 1) != position) {
            index = 4;
        }
        Bangumi leftBangumi = getData(group).get(index);
        holder.setText(R.id.bangumi_name_left, leftBangumi.getName());
        holder.setText(R.id.bangumi_ji, leftBangumi.getLatestJi());
        holder.setImageRes(R.id.bangumi_cover_left, leftBangumi.getCover());
        holder.setText(R.id.bangumi_hit_left, leftBangumi.getHits());
        holder.getView(R.id.parent_left).setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), leftBangumi));

        Bangumi rightBangumi = getData(group).get(index + 1);
        holder.setText(R.id.bangumi_name_right, rightBangumi.getName());
        holder.setText(R.id.bangumi_ji_right, rightBangumi.getLatestJi());
        holder.setImageRes(R.id.bangumi_cover_right, rightBangumi.getCover());
        holder.setText(R.id.bangumi_hit_right, rightBangumi.getHits());
        holder.getView(R.id.parent_right).setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), rightBangumi));
    }

    protected void bindTitle(int position, BaseViewHolder holder) {
        position = (position - 1) / 4;
        String title = titles[position];
        holder.setText(R.id.zzzfun_home_title, title);
        holder.getView(R.id.home_bangumi_more).setOnClickListener(v -> CategoryResultActivity.startCategoryResultActivity(getContext(), title));
    }

    protected int getBodyGroup(int position) {
        for (int i = 0; i < getDataList().size() - 1; i++) {
            int start = 2 * (i * 2 + 1);
            int end = start + 2;
            if (start <= position && position <= end) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADE;
        } else if (getBodyGroup(position) != -1) {
            return BODY;
        } else {
            return TITLE;
        }

    }

    @Override
    public int getItemCount() {
        int row = 0;
        for (int i = 0; i < getDataList().size() - 1; i++) {
            row += getData(i).size() / 2;
        }
        return getDataList().size() + row;
    }

    public ItemDecoration getItemDecoration() {
        if (mItemDecoration == null) {
            mItemDecoration = new ItemDecoration();
        }
        return mItemDecoration;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        super.onAttachedToRecyclerView(recyclerView);
    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int viewType = parent.getChildViewHolder(view).getItemViewType();
            if (viewType == BODY) {
                int position = parent.getChildAdapterPosition(view);
                int group = getBodyGroup(position);
                if (position % 2 == 1 || position == (group * 2 + 1) * 2) {
                    outRect.bottom = AnimationUtil.dp2px(view.getContext(), 10);
                }

            }
        }
    }

}


