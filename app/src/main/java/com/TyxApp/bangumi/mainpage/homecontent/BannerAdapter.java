package com.TyxApp.bangumi.mainpage.homecontent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.util.LogUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class BannerAdapter extends PagerAdapter {
    private List<Bangumi> mBangumiList;
    private Context mContext;

    public BannerAdapter(Context context) {
        mBangumiList = new ArrayList<>();
        mContext = context;
    }

    public void addAll(Collection<Bangumi> collection) {
        mBangumiList.clear();
        mBangumiList.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBangumiList.isEmpty() ? 0 : mBangumiList.size() + 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final int relposition = (position - 1 + mBangumiList.size()) % mBangumiList.size();
        LogUtil.i(relposition + "asdfasdf");
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_zzzfun_banner, null);
        ImageView imageView = view.findViewById(R.id.iv_zzzfun_banner);
        TextView textView = view.findViewById(R.id.tv_zzzfun_banner_name);
        Bangumi bangumi = mBangumiList.get(relposition);
        Glide.with(mContext).load(bangumi.getImg()).into(imageView);
        textView.setText(bangumi.getName());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
