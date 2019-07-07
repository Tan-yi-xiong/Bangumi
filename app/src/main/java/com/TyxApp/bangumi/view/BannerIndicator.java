package com.TyxApp.bangumi.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.TyxApp.bangumi.R;

import java.util.ArrayList;
import java.util.List;

public class BannerIndicator extends LinearLayout{
    private List<ImageView> ivList;

    public BannerIndicator(Context c) {
        super(c);
    }

    public BannerIndicator(Context c, AttributeSet as) {
        super(c, as);
    }

    public void setDotCount(int count) {
        ivList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(getContext());
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(15, 15);
            p.setMargins(10, 10, 10, 10);
            iv.setImageResource(R.drawable.indicator_dot);
            iv.setLayoutParams(p);
            if (i == 0) iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            ivList.add(iv);
            addView(iv);
        }
    }

    public void select(int pos) {
        if (pos > ivList.size() - 1 || pos < 0) {
            return;
        }
        for (int i = 0; i < ivList.size(); i++) {
            if (i == pos)
                ivList.get(i).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            else
                ivList.get(i).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }
}
