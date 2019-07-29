package com.TyxApp.bangumi.main.bangumi.adapter.zzzfun;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.categoryresult.CategoryResultActivity;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.bangumi.adapter.BaseHomeBangumiAdapter;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.view.BannerIndicator;
import com.TyxApp.bangumi.view.BannerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ZzzFunHomeBangumiAdapter extends BaseHomeBangumiAdapter<List<Bangumi>, BaseViewHolder> implements LifecycleObserver {
    private final static int HEADE = 0;
    private final static int BODY = 1;
    private final static int TITLE = 2;
    private BannerView mBannerView;
    private String[] titles;
    private ItemDecoration mItemDecoration;

    public ZzzFunHomeBangumiAdapter(Context context) {
        super(context);
        titles = context.getResources().getStringArray(R.array.zzfun_title);
    }

    @Override
    public void populaterBangumis(List<List<Bangumi>> bangumis) {
        getDataList().addAll(bangumis);
        notifyDataSetChanged();
    }

    @Override
    public void populaterNewBangumis(List<List<Bangumi>> newBangumis) {
        getDataList().clear();
        getDataList().addAll(newBangumis);
        if (mBannerView != null) {
            BannerAdapter adapter = (BannerAdapter) mBannerView.getAdapter();
            adapter.addAll(newBangumis.get(5));
        }
        notifyDataSetChanged();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (mBannerView != null) {
            mBannerView.startLunbo();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPuse() {
        if (mBannerView != null) {
            mBannerView.stopLunbo();
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_zzzfun_homeheader);
        } else if (viewType == TITLE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_zzzfuntitle);
        } else if (viewType == BODY) {
            return BaseViewHolder.get(getContext(), parent, R.layout.item_home_bangumi);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADE:
                if (mBannerView != null) {
                    break;
                }
                bindHeader(holder);
                break;
            case TITLE:
                bindTitle(position, holder);
                break;
            case BODY:
                bindBody(position, holder);
                break;
        }
    }

    private void bindBody(int position, BaseViewHolder holder) {
        int group = getBodyGroup(position);
        int index = 0;
        if (position % 2 == 1) {
            index = 2;
        } else if (2 * (group * 2 + 1) != position) {
            index = 4;
        }
        Bangumi leftBangumi = getData(group).get(index);
        holder.setText(R.id.bangumi_name_left, leftBangumi.getName());
        String jiTotal = getJiTotal(leftBangumi);
        holder.setText(R.id.bangumi_ji_left, jiTotal);
        holder.setImageRes(R.id.bangumi_cover_left, leftBangumi.getCover());
        holder.setText(R.id.bangumi_hit_left, leftBangumi.getHits());
        holder.getView(R.id.parent_left).setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), leftBangumi));

        Bangumi rightBangumi = getData(group).get(index + 1);
        holder.setText(R.id.bangumi_name_right, rightBangumi.getName());
        jiTotal = getJiTotal(rightBangumi);
        holder.setText(R.id.bangumi_ji_right, jiTotal);
        holder.setImageRes(R.id.bangumi_cover_right, rightBangumi.getCover());
        holder.setText(R.id.bangumi_hit_right, rightBangumi.getHits());
        holder.getView(R.id.parent_right).setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), rightBangumi));
    }

    private String getJiTotal(Bangumi bangumi) {
        String jiTotal = bangumi.getRemarks();
        if (TextUtils.isEmpty(jiTotal)) {
            StringBuilder builder = new StringBuilder();
            jiTotal = bangumi.getTotal();
            if (!TextUtils.isEmpty(jiTotal) && !"0".equals(jiTotal)) {
                builder.append("全");
                builder.append(jiTotal);
                builder.append("话");
            } else if (!TextUtils.isEmpty(bangumi.getSerial())){
                builder.append("更新至");
                builder.append(bangumi.getSerial());
                builder.append("话");
            }
            jiTotal = builder.toString();
        }
        return jiTotal;
    }

    private void bindTitle(int position, BaseViewHolder holder) {
        position = (position - 1) / 4;
        String title = titles[position];
        holder.setText(R.id.zzzfun_home_title, title);
        holder.getView(R.id.home_bangumi_more).setOnClickListener(v -> CategoryResultActivity.startCategoryActivity(getContext(), title));
    }

    private int getBodyGroup(int position) {
        for (int i = 0; i < getDataList().size() - 1; i++) {
            int start = 2 * (i * 2 + 1);
            int end = start + 2;
            if (start <= position && position <= end) {
                return i;
            }
        }
        return -1;
    }

    private void bindHeader(BaseViewHolder holder) {
        int bannerDataSize = getDataList().get(5).size();
        BannerIndicator indicator = holder.getView(R.id.banner_dots);
        indicator.setDotCount(bannerDataSize);

        mBannerView = holder.getView(R.id.banner);
        BannerAdapter adapter = new BannerAdapter(getContext());
        mBannerView.addOnPageChangeListener(new BannerView.MOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //计算得正确位置
                position = (position - 1 + bannerDataSize) % bannerDataSize;
                indicator.select(position);
            }
        });
        mBannerView.setAdapter(adapter);
        adapter.addAll(getDataList().get(5));
        mBannerView.startLunbo(2500);
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


