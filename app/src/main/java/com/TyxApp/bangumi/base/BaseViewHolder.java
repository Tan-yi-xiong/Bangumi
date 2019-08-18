package com.TyxApp.bangumi.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View rootView;
    private Context mContext;

    private BaseViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        mContext = context;
        rootView = itemView;
        mViews = new SparseArray<>();
    }

    public static BaseViewHolder get(Context context, ViewGroup content, int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, content, false);
        return new BaseViewHolder(context, view);
    }

    public static BaseViewHolder get(Context context, int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        return new BaseViewHolder(context, view);
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = rootView.findViewById(viewId);
            mViews.append(viewId, view);
        }
        return (T) view;
    }

    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    public void setImageRes(int viewId, Object res) {
        ImageView imageView = getView(viewId);
        Glide.with(mContext).load(res).error(R.drawable.image_error).into(imageView);
    }

    public void setRoundedImage(int viewId, Object res, int angle) {
        ImageView imageView = getView(viewId);
        Glide.with(mContext).load(res).error(R.drawable.image_error).transform(new CenterCrop(), new RoundedCorners(angle)).into(imageView);
    }

    public void setCircleImage(int viewId, Object res) {
        ImageView imageView = getView(viewId);
        Glide.with(mContext).load(res).error(R.drawable.image_error).transform(new CircleCrop()).into(imageView);
    }

}
