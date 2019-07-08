/*
 * 这个类是实现切换不同网站做主页的, BangumiFragment是用RecyclerView显示内容的, 所以只要切换Adapter就实现了切换网站做主页功能
 *
 *
 */


package com.TyxApp.bangumi.mainpage.homecontent.adapter;

import android.content.Context;


import com.TyxApp.bangumi.base.BaseAdapter;
import com.TyxApp.bangumi.base.BaseViewHolder;
import com.TyxApp.bangumi.data.Bangumi;

import java.util.Collection;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public abstract class BaseHomeBangumiAdapter<T, VH extends RecyclerView.ViewHolder> extends BaseAdapter<T, VH> {
    public BaseHomeBangumiAdapter(Context context) {
        super(context);
    }

    /**
     * 第一次显示主页时加载完view层会调用这个方法.
     *
     */
    public abstract void populaterBangumis(List<List<Bangumi>> bangumis);

    /**
     * 刷新后得到后数据view层会回调这个方法
     *
     */
    public abstract void populaterNewBangumis(List<List<Bangumi>> newBangumis);

}
