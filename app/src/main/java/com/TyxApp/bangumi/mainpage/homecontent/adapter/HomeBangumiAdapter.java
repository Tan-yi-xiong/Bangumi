package com.TyxApp.bangumi.mainpage.homecontent.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.mainpage.homecontent.adapter.BannerAdapter;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.view.BannerIndicator;
import com.TyxApp.bangumi.view.BannerView;

import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeBangumiAdapter extends BaseHomeBangumiAdapter<List<Bangumi>, BaseViewHolder> implements LifecycleObserver {
    private final static int HEADE = 0;
    private final static int BODY = 1;
    private final static int TITLE = 2;
    private BannerView mBannerView;
    private String[] titles;

    public HomeBangumiAdapter(Context context) {
        super(context);
        titles = getContext().getResources().getStringArray(R.array.zzfun_title);
    }

    @Override
    public void populaterBangumis(List<List<Bangumi>> bangumis) {
        getDataList().addAll(bangumis);
        notifyDataSetChanged();
    }

    @Override
    public void populaterNewBangumis(List<List<Bangumi>> newBangumis) {
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
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_home_bangumi_item);
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
        //计算真正的下标
        int group = getWichGroup(position);
        int gropChildCount = getDataList().get(group).size();
        position = ((gropChildCount + 1) * (group + 1) - position) - 5;
        position = Math.abs(position);
        Bangumi bangumi = getDataList().get(group).get(position);
        holder.setImageRes(R.id.iv_bangumi_cover, bangumi.getCover());
        holder.setText(R.id.tv_bangumi_name, bangumi.getName());
        holder.setText(R.id.tv_bangumi_hit, bangumi.getHits());
        String ji = bangumi.getRemarks();
        if (TextUtils.isEmpty(ji)) {
            if (Integer.valueOf(bangumi.getTotal()) == 0) {
                ji = "更新至" + bangumi.getSerial() + "话";
            } else {
                ji = "全" + bangumi.getTotal() + "话";
            }
        }
        holder.setText(R.id.tv_bangumi_ji, ji);
    }

    /**
     * 获取是哪组数据, zzzfun首页6个为1组, 也可以判断是否显示番剧条目
     * 规律：2 - 7, 9 - 14 ....
     * 最后一个都为7的倍数
     */
    private int getWichGroup(int pos) {
        for (int i = 0; i < getDataList().size() - 1; i++) {
            int end = (getDataList().get(i).size() + 1) * (i + 1);
            int start = end - 5;
            if (start <= pos && pos <= end) {
                return i;
            }
        }
        return -1;
    }

    private void bindTitle(int position, BaseViewHolder holder) {
        //获取对应标题下标
        position = position % 6 - 1;
        holder.setText(R.id.zzzfun_home_title, titles[position]);
        ImageButton imageButton = holder.getView(R.id.home_bangumi_more);
        if (position == 0) {
            imageButton.setVisibility(View.GONE);
        } else {
            imageButton.setVisibility(View.VISIBLE);
        }
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
    public int getItemCount() {
        int bangumiSize = 0;
        for (int i = 0; i < getDataList().size(); i++) {
            //不记头部数量
            if (i == 5) {
                continue;
            }
            bangumiSize += getDataList().get(i).size();
        }
        return getDataList().size() + bangumiSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADE;
        } else if (position % 7 == 1) {
            return TITLE;
        }
        return BODY;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager layoutManager = recyclerView.getLayoutManager() instanceof GridLayoutManager ?
                (GridLayoutManager) recyclerView.getLayoutManager() : null;

        if (layoutManager != null) {
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    //头部和标题部分宽度占满
                    return viewType == HEADE || viewType == TITLE ? layoutManager.getSpanCount() : 1;
                }
            });
        }
    }
    public static class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int viewType = parent.getChildViewHolder(view).getItemViewType();
            if (viewType == BODY) {
                outRect.bottom = AnimationUtil.dp2px(view.getContext(), 10);
            }
        }
    }
}
