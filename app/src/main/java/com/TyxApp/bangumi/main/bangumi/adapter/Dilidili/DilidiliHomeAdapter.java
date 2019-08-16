package com.TyxApp.bangumi.main.bangumi.adapter.Dilidili;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.categoryresult.CategoryResultActivity;
import com.TyxApp.bangumi.main.bangumi.adapter.DefaultHomeAdapter;
import com.TyxApp.bangumi.main.bangumi.adapter.GroupChildAdapter;
import com.TyxApp.bangumi.util.AnimationUtil;

public class DilidiliHomeAdapter extends DefaultHomeAdapter {
    public DilidiliHomeAdapter(Activity activity) {
        super(activity);
    }

    @Override
    protected void bindBody(int position, BaseViewHolder holder) {
        position = position - 1;
        String title = (String) getGroupBangumis().keySet().toArray()[position];
        View view = holder.getView(R.id.title_view);
        holder.setText(R.id.home_title, title);
        RecyclerView recyclerView = holder.getView(R.id.content_RecyclerView);
        BaseAdapter adapter = (BaseAdapter) recyclerView.getAdapter();
        view.setOnClickListener(v -> CategoryResultActivity.startCategoryResultActivity(getContext(), title));
        if (adapter == null) {
            if (title.equals("最近更新")) {
                adapter = new NewBangumiChildAdapter(getContext());
            } else {
                adapter = new GroupChildAdapter(getContext());
            }
            recyclerView.setAdapter(adapter);
        }
        if (title.contains("补番")) {
            holder.getView(R.id.text_more).setVisibility(View.GONE);
            view.setOnClickListener(v -> {
            });
        } else if ("最近更新".equals(title)) {
            view.setOnClickListener(v -> MoreNewBangumiFragment.newInstance().show(((AppCompatActivity) getContext()).getSupportFragmentManager(), MoreNewBangumiFragment.class.getName()));
        }
        adapter.clearAddAll(getGroupBangumis().get(title));
    }


}
