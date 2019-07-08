package com.TyxApp.bangumi.util;

import android.content.Context;

public class AnimationUtil {
    public static int dp2px(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5);
    }
}
