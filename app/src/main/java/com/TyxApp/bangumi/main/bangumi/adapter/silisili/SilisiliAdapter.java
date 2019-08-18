package com.TyxApp.bangumi.main.bangumi.adapter.silisili;

import android.content.Context;
import android.view.View;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.main.bangumi.adapter.DefaultHomeAdapter;

public class SilisiliAdapter extends DefaultHomeAdapter {
    public SilisiliAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindBody(int position, BaseViewHolder holder) {
        super.bindBody(position, holder);
        holder.getView(R.id.text_more).setVisibility(View.GONE);
        holder.getView(R.id.title_view).setOnClickListener(v -> {});
    }
}
