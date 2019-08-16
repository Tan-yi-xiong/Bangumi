package com.TyxApp.bangumi.main.search.SearchResult.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.player.adapter.TranslationAnimation;
import com.TyxApp.bangumi.player.PlayerActivity;
import com.TyxApp.bangumi.util.AnimationUtil;

public class SearchResultRVAdapter extends BaseAdapter<Bangumi, BaseViewHolder> {

    public SearchResultRVAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_search_result);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Bangumi bangumi = getData(position);
        holder.setImageRes(R.id.cover, bangumi.getCover());
        holder.setText(R.id.name, bangumi.getName());
        holder.setText(R.id.bangumi_ji_total, bangumi.getLatestJi());
        holder.itemView.setOnClickListener(v -> PlayerActivity.startPlayerActivity(getContext(), bangumi));
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new ItemDecoration());
        TranslationAnimation translationAnimation = new TranslationAnimation();
        translationAnimation.setStartTranslationY(500);
        translationAnimation.setAddDuration(250);
        recyclerView.setItemAnimator(translationAnimation);
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int distance = AnimationUtil.dp2px(view.getContext(), 8);
            outRect.bottom = distance;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = distance;
            }
        }
    }

}
