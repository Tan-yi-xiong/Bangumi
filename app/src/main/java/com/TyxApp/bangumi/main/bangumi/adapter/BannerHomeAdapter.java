/*
 * 这个类是实现切换不同网站做主页的, BangumiFragment是用RecyclerView显示内容的, 所以只要切换Adapter就实现了切换网站做主页功能
 *
 *
 */


package com.TyxApp.bangumi.main.bangumi.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.view.BannerIndicator;
import com.TyxApp.bangumi.view.BannerView;

import java.util.List;
import java.util.Map;


public abstract class BannerHomeAdapter<VH extends BaseViewHolder> extends BaseHomeAdapter<VH> implements LifecycleObserver  {
    protected final static int HEADE = 0;
    protected final static int BODY = 1;
    private BannerView mBannerView;
    public static final String BANNER_KEY = "banner";
    private Context mContext;
    private List<Bangumi> bannerBangumis;

    public BannerHomeAdapter(Context context) {
        mContext = context;
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

    @Override
    public void populaterBangumis(Map<String, List<Bangumi>> homebangumis) {
        bannerBangumis = homebangumis.get(BANNER_KEY);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADE:
                bindHeader(holder);
                break;
            case BODY:
                bindBody(position, holder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADE;
        } else {
            return BODY;
        }
    }

    protected abstract void bindBody(int position, VH holder);


    private void bindHeader(BaseViewHolder holder){
        int bannerDataSize = bannerBangumis.size();
        BannerIndicator indicator = holder.getView(R.id.banner_dots);
        indicator.setDotCount(bannerDataSize);
        mBannerView = holder.getView(R.id.banner);
        BannerAdapter adapter = (BannerAdapter) mBannerView.getAdapter();
        if (adapter == null) {
            adapter = new BannerAdapter(mContext);
            adapter.addAll(bannerBangumis);
            mBannerView.addOnPageChangeListener(new BannerView.extendsOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    //计算得正确位置
                    if (bannerDataSize == 0) {
                        return;
                    }
                    position = (position - 1 + bannerDataSize) % bannerDataSize;
                    indicator.select(position);
                }
            });
            mBannerView.setAdapter(adapter);
            mBannerView.startLunbo(1500);
        } else {
            adapter.addAll(bannerBangumis);
        }
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }
}
