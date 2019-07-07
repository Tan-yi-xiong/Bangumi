package com.TyxApp.bangumi.mainpage.homecontent;

import android.content.Context;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.Bangumi;
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

public class HomeBangumiAdapter extends BaseAdapter<List<Bangumi>, BaseViewHolder> implements LifecycleObserver {
    private final static int HEADE = 0;
    private final static int BODY = 1;
    private final static int TITLE = 2;
    private BannerView mBannerView;

    public HomeBangumiAdapter(Context context) {
        super(context);
    }

    public void headerBangumisAddAll(Collection<Bangumi> collection) {
        if (mBannerView != null) {
            BannerAdapter adapter = (BannerAdapter) mBannerView.getAdapter();
            adapter.addAll(collection);
            //notifyItemChanged(0);
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

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_zzzfun_homeheader);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder.getItemViewType() == HEADE) {
            bindHeader(holder);
        }
    }

    private void bindHeader(BaseViewHolder holder) {
        mBannerView = holder.getView(R.id.banner);
        mBannerView.setAdapter(new BannerAdapter(getContext()));
        mBannerView.startLunbo(2500);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager layoutManager = recyclerView.getLayoutManager() instanceof GridLayoutManager ?
                (GridLayoutManager)recyclerView.getLayoutManager() : null;

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
}
