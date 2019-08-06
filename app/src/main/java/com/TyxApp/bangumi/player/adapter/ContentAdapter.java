package com.TyxApp.bangumi.player.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.util.AnimationUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContentAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Bangumi mBangumi;
    private List<Bangumi> mRecommendBangumis;

    private Context mContext;
    private JiAdapter mJiAdapter;
    private OnItemSelectListener mOnItemSelectListener;

    private static final int BANGUMI_INTRO = 0;//简介部分
    private static final int JI_SELECT = 1;//选集部分
    private static final int TITLE = 2;//更多推荐标题
    private static final int RECOMMENBANGUMI = 3;//更多推荐
    private RecyclerView mJiRecyclerView;

    public ContentAdapter(Bangumi bangumi, Context context) {
        mBangumi = bangumi;
        mRecommendBangumis = new ArrayList<>();
        mContext = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BANGUMI_INTRO) {
            return BaseViewHolder.get(parent.getContext(), parent, R.layout.item_player_bangumi_intro);
        } else if (viewType == JI_SELECT) {
            return BaseViewHolder.get(parent.getContext(), parent, R.layout.item_player_ji_selsct);
        } else if (viewType == TITLE) {
            return BaseViewHolder.get(parent.getContext(), parent, R.layout.item_player_title);
        } else {
            return BaseViewHolder.get(parent.getContext(), parent, R.layout.item_player_recommend);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder.getItemViewType() == BANGUMI_INTRO) {
            bindIntro(holder);
        } else if (holder.getItemViewType() == JI_SELECT) {
            bindJiSelect(holder);
        } else if (holder.getItemViewType() == RECOMMENBANGUMI) {
            bindBangumi(position, holder);
        }
    }

    private void bindBangumi(int position, BaseViewHolder holder) {
        position = position - 3;
        Bangumi bangumi = mRecommendBangumis.get(position);
        int angle = AnimationUtil.dp2px(mContext, 5);
        holder.setRoundedImage(R.id.recommend_cover, bangumi.getCover(), angle);
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.ji_total, bangumi.getLatestJi());

        holder.itemView.setOnClickListener(v -> {
            if (mOnItemSelectListener != null) {
                mOnItemSelectListener.onBangumiSelect(bangumi);
            }
        });
    }

    private void bindJiSelect(BaseViewHolder holder) {
        if (mJiAdapter == null) {
            mJiRecyclerView = holder.getView(R.id.ji_select_recyclerView);
            mJiAdapter = new JiAdapter(mContext);
            mJiAdapter.setOnItemClickListener(pos -> {
                setJiSelect(pos);
                if (mOnItemSelectListener != null) {
                    mOnItemSelectListener.onJiSelect(pos);
                }
            });
            mJiRecyclerView.addItemDecoration(new JiAdapter.ItemDecoration());
            mJiRecyclerView.setAdapter(mJiAdapter);
            mJiRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        }
    }

    public void setJiSelect(int pos) {
        int lastSelectPosition = 0;
        for (int i = 0; i < mJiAdapter.getItemCount(); i++) {
            if (mJiAdapter.getData(i).isSelect()) {
                lastSelectPosition = i;
            }
        }
        if (lastSelectPosition != pos) {
            mJiAdapter.getData(pos).setSelect(true);
            RecyclerView.ViewHolder jiViewHolder = mJiRecyclerView.findViewHolderForLayoutPosition(pos);
            RecyclerView.ViewHolder laseSelectViewHolder = mJiRecyclerView.findViewHolderForLayoutPosition(lastSelectPosition);
            if (jiViewHolder != null) {
                jiViewHolder.itemView.setSelected(true);
            }
            if (laseSelectViewHolder != null) {
                laseSelectViewHolder.itemView.setSelected(false);
            }
            mJiAdapter.getData(pos).setSelect(true);
            mJiAdapter.getData(lastSelectPosition).setSelect(false);
        }
    }

    private void bindIntro(BaseViewHolder holder) {
        int angle = AnimationUtil.dp2px(mContext, 3);
        holder.setRoundedImage(R.id.cover, mBangumi.getCover(), angle);
        holder.setText(R.id.name, mBangumi.getName());
        holder.setText(R.id.ji_total, mBangumi.getLatestJi());

        holder.setText(R.id.intro, mBangumi.getIntro());

        Button favoriteButton = holder.getView(R.id.collect_button);
        if (mBangumi.isFavorite()) {
            favoriteButton.setSelected(true);
            favoriteButton.setText("已追番");
        } else {
            favoriteButton.setSelected(false);
            favoriteButton.setText("追番");
        }
        favoriteButton.setOnClickListener(v -> {
            if (mOnItemSelectListener != null) {
                mOnItemSelectListener.onFavoriteButtonClick(v.isSelected());
            }

        });
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return BANGUMI_INTRO;
        } else if (position == 1) {
            return JI_SELECT;
        } else if (position == 2) {
            return TITLE;
        } else {
            return RECOMMENBANGUMI;
        }
    }

    @Override
    public int getItemCount() {
        return mRecommendBangumis.size() + 3;
    }


    public List<TextItemSelectBean> getJiList() {
        return mJiAdapter.getDataList();
    }

    public void notifiBangumiChange(Bangumi bangumi) {
        mBangumi = bangumi;
        notifyItemChanged(0);
    }


    public void notifiBangumiChange() {
        notifyItemChanged(0);
    }

    public void notifijiListChange(List<TextItemSelectBean> jiList) {
        mJiAdapter.clearAddAll(jiList);
    }

    public void notifiRecommendBangumisChange(List<Bangumi> recommendBangumis) {
        if (!mRecommendBangumis.isEmpty()) {
            int size = mRecommendBangumis.size();
            mRecommendBangumis.clear();
            notifyItemRangeRemoved(3, size);
        }
        mRecommendBangumis.addAll(recommendBangumis);
        notifyItemRangeChanged(3, mRecommendBangumis.size());
    }

    public interface OnItemSelectListener {
        void onJiSelect(int pos);

        void onBangumiSelect(Bangumi bangumi);

        void onFavoriteButtonClick(boolean isCollect);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }
}
