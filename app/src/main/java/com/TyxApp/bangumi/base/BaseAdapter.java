package com.TyxApp.bangumi.base;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> dataList;
    private Context mContext;
    public OnItemClickListener mOnItemClickListener;

    public BaseAdapter(Context context) {
        dataList = new ArrayList<>();
        mContext = context;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public BaseAdapter(List<T> dataList, Context context) {
        this.dataList = dataList;
        mContext = context;
    }

    public void add(T t) {
        dataList.add(t);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addAll(Collection<T> collection) {
        int oldSize = dataList.size();
        dataList.addAll(collection);
        notifyItemRangeChanged(oldSize, collection.size());
    }

    public T getData(int pos) {
        return dataList.get(pos);
    }

    public Context getContext() {
        return mContext;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
