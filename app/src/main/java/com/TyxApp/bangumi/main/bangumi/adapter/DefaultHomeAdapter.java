package com.TyxApp.bangumi.main.bangumi.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.categoryresult.CategoryResultActivity;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.adapter.TranslationAnimation;
import com.TyxApp.bangumi.util.AnimationUtil;

import java.util.List;
import java.util.Map;

public class DefaultHomeAdapter extends BannerHomeAdapter<BaseViewHolder> {
    private Map<String, List<Bangumi>> mGroupBangumis;

    public DefaultHomeAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindBody(int position, BaseViewHolder holder) {
        RecyclerView recyclerView = holder.getView(R.id.content_RecyclerView);
        position = position - 1;
        View view = holder.getView(R.id.title_view);
        String title = (String) mGroupBangumis.keySet().toArray()[position];
        view.setOnClickListener(v -> CategoryResultActivity.startCategoryResultActivity(getContext(), title));
        holder.setText(R.id.home_title, title);
        GroupChildAdapter adapter = (GroupChildAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new GroupChildAdapter(getContext());
            recyclerView.setAdapter(adapter);
        }
        adapter.clearAddAll(mGroupBangumis.get(title));
    }

    @Override
    public void populaterBangumis(Map<String, List<Bangumi>> homebangumis) {
        super.populaterBangumis(homebangumis);
        boolean isFristPopulater = mGroupBangumis == null;
        homebangumis.put(BANNER_KEY, homebangumis.remove(BANNER_KEY));//轮播图那组移到最后, 不然会出现内容错位
        mGroupBangumis = homebangumis;
        if (isFristPopulater) {
            notifyItemRangeChanged(1, homebangumis.size() - 1);
        } else {
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADE) {
            return BaseViewHolder.get(getContext(), parent, R.layout.layout_home_header);
        }
        return BaseViewHolder.get(getContext(), parent, R.layout.item_home_bangumis);
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new ItemDecoration());
        TranslationAnimation translationAnimation = new TranslationAnimation();
        translationAnimation.setAddDuration(250);
        translationAnimation.setStartTranslationY(1000);
        recyclerView.setItemAnimator(translationAnimation);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mGroupBangumis == null ? 0 : mGroupBangumis.size();
    }


    public Map<String, List<Bangumi>> getGroupBangumis() {
        return mGroupBangumis;
    }

    class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int margin = AnimationUtil.dp2px(getContext(), 8);
            outRect.top = margin;
            int postion = parent.getChildAdapterPosition(view);
            if (postion == getItemCount() - 1) {
                outRect.bottom = margin;
            }
        }
    }
}
