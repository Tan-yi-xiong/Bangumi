package com.TyxApp.bangumi.main.category.adapter;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends BaseAdapter<CategorItem, BaseViewHolder> {
    public CategoryAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.get(getContext(), parent, R.layout.item_grid_category);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        CategorItem item = getData(position);
        holder.setRoundedImage(R.id.cover, item.getImageRes(), AnimationUtil.dp2px(getContext(), 3));
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
