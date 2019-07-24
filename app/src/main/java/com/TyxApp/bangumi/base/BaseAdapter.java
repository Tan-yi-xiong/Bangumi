package com.TyxApp.bangumi.base;

import android.content.Context;
import android.util.Log;

import com.TyxApp.bangumi.data.bean.Bangumi;
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
    public onItemLongClickLisener mOnItemLongClickLisener;

    public BaseAdapter(Context context) {
        this(new ArrayList<>(), context);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public BaseAdapter(List<T> dataList, Context context) {
        this.dataList = dataList;
        mContext = context;
    }

    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    public void addInserted(T t, int index) {
        dataList.add(index, t);
        notifyItemInserted(index);
    }



    public void clearAddAll(Collection<T> collection) {
        if (!dataList.isEmpty()) {
            notifyItemRangeRemoved(0, dataList.size());
            dataList.clear();
        }
        addAllInserted(collection);
    }

    public void remove(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
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

    public interface onItemLongClickLisener {
        boolean onItemLongClick(Bangumi bangumi);
    }

    public void setOnItemLongClickLisener(onItemLongClickLisener onItemLongClickLisener) {
        mOnItemLongClickLisener = onItemLongClickLisener;
    }

}
