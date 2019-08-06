/*
 * 这个类是实现切换不同网站做主页的, BangumiFragment是用RecyclerView显示内容的, 所以只要切换Adapter就实现了切换网站做主页功能
 *
 *
 */


package com.TyxApp.bangumi.main.bangumi.adapter;

import android.content.Context;
import android.view.ViewGroup;


import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.main.bangumi.adapter.zzzfun.ZzzFunHomeAdapter;
import com.TyxApp.bangumi.view.BannerIndicator;
import com.TyxApp.bangumi.view.BannerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;


public abstract class BannerHomeAdapter<VH extends BaseViewHolder> extends BaseAdapter<List<Bangumi>, VH>  implements LifecycleObserver {
    public final static int HEADE = 0;
    public final static int BODY = 1;
    public final static int TITLE = 2;
    private BannerView mBannerView;

    public BannerHomeAdapter(Context context) {
        super(context);
    }

    /**
     * 第一次显示主页时加载完view层会调用这个方法.
     *
     */
    public void populaterBangumis(List<List<Bangumi>> bangumis){
        if (mBannerView != null) {
            BannerAdapter adapter = (BannerAdapter) mBannerView.getAdapter();
            adapter.addAll(bangumis.get(getDataList().size() - 1));
        }
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
    public void onBindViewHolder(@NonNull VH holder, int position) {
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

    protected abstract void bindBody(int position, VH holder);

    protected abstract void bindTitle(int position, BaseViewHolder holder);

    protected void bindHeader(BaseViewHolder holder){
        int bannerDataSize = getDataList().get(getDataList().size() - 1).size();
        BannerIndicator indicator = holder.getView(R.id.banner_dots);
        indicator.setDotCount(bannerDataSize);

        mBannerView = holder.getView(R.id.banner);
        BannerAdapter adapter = new BannerAdapter(getContext());
        mBannerView.addOnPageChangeListener(new BannerView.extendsOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //计算得正确位置
                position = (position - 1 + bannerDataSize) % bannerDataSize;
                indicator.select(position);
            }
        });
        mBannerView.setAdapter(adapter);
        adapter.addAll(getDataList().get(getDataList().size() - 1));
        mBannerView.startLunbo(2500);
    }


}
