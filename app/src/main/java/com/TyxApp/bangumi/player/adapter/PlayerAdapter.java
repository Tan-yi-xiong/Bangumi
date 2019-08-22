package com.TyxApp.bangumi.player.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.player.PlayerActivity;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ViewGroup infoViewGroup;
    private PlayerActivity mActivity;
    private OnItemClckListemer mOnItemClckListemer;

    private static final int SHOW_MORE = 5;
    private static final int SHOW_JILIST = 4;
    private static final int PARSE_ERROR = 3;
    private static final int NO_DATA = 2;
    private static final int LOADING = 1;
    private static final int INFO_VIEW = 0;

    private List<TextItemSelectBean> jiList;
    private List<Bangumi> recommenBangumis;
    private int jiListCount = -1;//集数
    private JiAdapter mJiAdapter;


    public PlayerAdapter(ViewGroup infoViewGroup, PlayerActivity activity) {
        this.infoViewGroup = infoViewGroup;
        mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == INFO_VIEW) {
            return new RecyclerView.ViewHolder(infoViewGroup) {
            };
        } else if (viewType == LOADING) {
            return creatLoadingViewHolder(parent);
        } else if (viewType == NO_DATA) {
            return creatNoDataViewHolder(parent);
        } else if (viewType == PARSE_ERROR) {
            return creatErrorViewHolder(parent);
        } else if (viewType == SHOW_JILIST) {
            return creatJiListViewHolder(parent);
        } else {
            return creatMoreBangumiViewHolder(parent);
        }
    }

    private RecyclerView.ViewHolder creatMoreBangumiViewHolder(ViewGroup parent) {
        RecyclerView.ViewHolder viewHolder;
        if (recommenBangumis == null) {
            viewHolder = BaseViewHolder.get(mActivity, R.layout.layout_viewstub);
        } else {
            View inflaterView = LayoutInflater.from(mActivity).inflate(R.layout.layout_viewstub, null);
            ViewStub viewStub = inflaterView.findViewById(R.id.ViewStub);
            ViewGroup rootView = (ViewGroup) viewStub.inflate();
            rootView.getLayoutParams().width = parent.getWidth();
            RecyclerView recyclerView = rootView.findViewById(R.id.recommendRecyclerView);
            RecommendAdapter adapter = new RecommendAdapter(mActivity);
            adapter.setOnItemClickListener(pos -> {
                if (mOnItemClckListemer != null) {
                    mOnItemClckListemer.onbangumiItemSelect();
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.addAllInserted(recommenBangumis);
            viewHolder = new RecyclerView.ViewHolder(inflaterView) {
            };
        }
        return viewHolder;
    }

    private RecyclerView.ViewHolder creatJiListViewHolder(ViewGroup parent) {
        BaseViewHolder viewHolder = BaseViewHolder.get(mActivity, parent, R.layout.item_player_ji_choose);
        RecyclerView recyclerView = viewHolder.getView(R.id.ji_select_recyclerView);
        mJiAdapter = new JiAdapter(mActivity);
        recyclerView.setAdapter(mJiAdapter);
        mJiAdapter.clearAddAll(jiList);
        mJiAdapter.setOnItemClickListener(pos -> {
            if (mOnItemClckListemer != null) {
                mOnItemClckListemer.onJiClick(pos);
            }
        });
        return viewHolder;
    }

    private RecyclerView.ViewHolder creatErrorViewHolder(ViewGroup parent) {
        BaseViewHolder baseViewHolder = BaseViewHolder.get(mActivity, parent, R.layout.item_no_ji);
        baseViewHolder.setText(R.id.tips, "解析发生了错误>_<");
        return baseViewHolder;
    }

    private RecyclerView.ViewHolder creatNoDataViewHolder(ViewGroup parent) {
        return BaseViewHolder.get(mActivity, parent, R.layout.item_no_ji);
    }

    private RecyclerView.ViewHolder creatLoadingViewHolder(ViewGroup parent) {
        return BaseViewHolder.get(mActivity, parent, R.layout.item_player_loading);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    public void setJiList(List<TextItemSelectBean> jiList) {
        if (jiList == null) {
            jiListCount = -2;
        } else {
            jiListCount = jiList.size();
            this.jiList = jiList;
        }
        notifyItemChanged(1);
    }

    public void setRecommenBangumis(List<Bangumi> recommenBangumis) {
        this.recommenBangumis = recommenBangumis;
        notifyItemChanged(2);
    }

    public void setOnItemClckListemer(OnItemClckListemer onItemClckListemer) {
        mOnItemClckListemer = onItemClckListemer;
    }

    /**
     * 设置选中的集
     *
     */
    public void setJiSelect (int position) {
        if (mJiAdapter != null) {
            mJiAdapter.jiItemSelect(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return INFO_VIEW;
            case 1:
                if (jiListCount == -1) {
                    return LOADING;
                } else if (jiListCount == 0) {
                    return NO_DATA;
                } else if (jiListCount == -2) {
                    return PARSE_ERROR;
                } else {
                    return SHOW_JILIST;
                }
            case 2:
                return SHOW_MORE;

        }
        return super.getItemViewType(position);
    }

    public List<TextItemSelectBean> getJiList() {
        return jiList;
    }

    public interface OnItemClckListemer {
        void onJiClick(int position);

        void onbangumiItemSelect();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        TranslationAnimation translationAnimation = new TranslationAnimation();
        translationAnimation.setStartTranslationY(200);
        translationAnimation.setChangeDuration(250);
        recyclerView.setItemAnimator(translationAnimation);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mJiAdapter.clear();
        recommenBangumis.clear();
        recyclerView.clearAnimation();
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
