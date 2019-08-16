package com.TyxApp.bangumi.main.bangumi.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.data.bean.Bangumi;

import java.util.List;
import java.util.Map;

public abstract class BaseHomeAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public abstract void populaterBangumis(Map<String, List<Bangumi>> Homebangumis);
}
