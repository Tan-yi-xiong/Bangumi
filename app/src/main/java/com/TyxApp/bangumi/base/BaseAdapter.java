package com.TyxApp.bangumi.base;

import android.content.Context;

import com.TyxApp.bangumi.util.LogUtil;

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

    protected List<T> getDataList() {
        return dataList;
    }

    public BaseAdapter(List<T> dataList, Context context) {
        this.dataList = dataList;
        mContext = context;
    }

    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    public void addInserted(T t) {
        dataList.add(t);
        notifyItemInserted(dataList.size() - 1);
    }

    public void clearAddAll(Collection<T> collection) {
        dataList.clear();
        dataList.addAll(collection);
        notifyDataSetChanged();
    }

    public void addAllInserted(Collection<T> collection) {
        int oldSize = dataList.size();
        dataList.addAll(collection);
        notifyItemRangeInserted(oldSize, collection.size());
    }

    public void clear() {
        dataList.clear();
    }

    public T getData(int pos) {
        return dataList.get(pos);
    }

    public Context getContext() {
        return mContext;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
