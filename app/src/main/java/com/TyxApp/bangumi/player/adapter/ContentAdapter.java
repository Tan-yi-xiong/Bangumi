package com.TyxApp.bangumi.player.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContentAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Bangumi mBangumi;
    private List<Bangumi> mRecommendBangumis;

    private Context mContext;
    private JiAdapter mJiAdapter;
    private int lastSelectPosition;
    private BaseAdapter.OnItemClickListener mOnItemClickListener;

    private static final int BANGUMI_INTRO = 0;//简介部分
    private static final int JI_SELECT = 1;//选集部分
    private static final int TITLE = 2;//更多推荐标题
    private static final int RECOMMENBANGUMI = 3;//更多推荐

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
        String ji = bangumi.getRemarks();
        if (TextUtils.isEmpty(ji)) {
            if (!TextUtils.isEmpty(bangumi.getTotal())) {
                if (Integer.valueOf(bangumi.getTotal()) == 0) {
                    ji = "更新至" + bangumi.getSerial() + "话";
                } else {
                    ji = "全" + bangumi.getTotal() + "话";
                }
            }
        }
        holder.setText(R.id.ji_total, ji);
    }

    private void bindJiSelect(BaseViewHolder holder) {
        if (mJiAdapter == null) {
            RecyclerView recyclerView = holder.getView(R.id.ji_select_recyclerView);
            mJiAdapter = new JiAdapter(mContext);
            mJiAdapter.setOnItemClickListener(pos -> {
                if (lastSelectPosition != pos) {
                    mJiAdapter.getData(pos).setSelect(true);
                    RecyclerView.ViewHolder jiViewHolder = recyclerView.findViewHolderForLayoutPosition(pos);
                    RecyclerView.ViewHolder laseSelectViewHolder = recyclerView.findViewHolderForLayoutPosition(lastSelectPosition);
                    if (jiViewHolder != null) {
                        jiViewHolder.itemView.setSelected(true);
                    }
                    if (laseSelectViewHolder != null) {
                        laseSelectViewHolder.itemView.setSelected(false);
                    }
                    mJiAdapter.getData(pos).setSelect(true);
                    mJiAdapter.getData(lastSelectPosition).setSelect(false);
                    lastSelectPosition = pos;
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(pos);
                }
            });
            recyclerView.addItemDecoration(new JiAdapter.ItemDecoration());
            recyclerView.setAdapter(mJiAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        }
    }

    private void bindIntro(BaseViewHolder holder) {
        int angle = AnimationUtil.dp2px(mContext, 3);
        holder.setRoundedImage(R.id.bangumi_cover, mBangumi.getCover(), angle);
        holder.setText(R.id.bangumi_name, mBangumi.getName());
        String ji = mBangumi.getRemarks();
        if (TextUtils.isEmpty(ji)) {
            if (!TextUtils.isEmpty(mBangumi.getTotal())) {
                if (Integer.valueOf(mBangumi.getTotal()) == 0) {
                    ji = "更新至" + mBangumi.getSerial() + "话";
                } else {
                    ji = "全" + mBangumi.getTotal() + "话";
                }
            }
        }
        holder.setText(R.id.ji_total, ji);

        holder.setText(R.id.intro, mBangumi.getIntro());
    }

    public TextItemSelectBean getJiData(int position) {
        return mJiAdapter.getData(position);
    }

    @Override
    public int getItemCount() {
        return mRecommendBangumis.size() + 3;
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


    public void notifiBangumiChange(Bangumi bangumi) {
        mBangumi = bangumi;
        notifyItemChanged(0);
    }

    public void notifiBangumiChange() {
        notifyItemChanged(0);
    }

    public void notifijiListChange(List<TextItemSelectBean> jiList) {
        mJiAdapter.addAllInserted(jiList);
    }

    public void notifiRecommendBangumisChange(List<Bangumi> recommendBangumis) {
        if (!mRecommendBangumis.isEmpty()) {
            notifyItemMoved(3, mRecommendBangumis.size());
            mRecommendBangumis.clear();
        }
        mRecommendBangumis.addAll(recommendBangumis);
        notifyItemRangeChanged(3, mRecommendBangumis.size());
    }

    public void setOnJiSelectListener(BaseAdapter.OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }
}
