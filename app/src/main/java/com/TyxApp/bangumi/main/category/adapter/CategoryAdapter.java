package com.TyxApp.bangumi.main.category.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.categoryresult.CategoryResultActivity;
import com.TyxApp.bangumi.data.bean.CategorItem;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends BaseAdapter<CategorItem, BaseViewHolder> {
    public CategoryAdapter(Context context) {
        super(context);
    }

    private int lastAnimaPoaition = -1;

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_category);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        CategorItem item = getData(position);
        ImageView imageView = holder.getView(R.id.cover);
        if (lastAnimaPoaition < position) {
            Glide.with(getContext())
                    .load(item.getImageRes())
                    .circleCrop()
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                            AnimationUtil.popAnima(imageView);
                        }
                    });
            lastAnimaPoaition = holder.getAdapterPosition();
        } else {
            holder.setCircleImage(R.id.cover, item.getImageRes());
        }
        TextView textView = holder.getView(R.id.name);
        textView.setGravity(Gravity.CENTER);
        textView.setText(item.getName());
        holder.itemView.setOnClickListener(v -> CategoryResultActivity.startCategoryResultActivity(getContext(), item.getName()));
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        super.onAttachedToRecyclerView(recyclerView);
    }

}
