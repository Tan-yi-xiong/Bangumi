package com.TyxApp.bangumi.util;

import android.animation.ValueAnimator;
import android.content.Context;

import androidx.appcompat.app.ActionBarDrawerToggle;

public class AnimationUtil {
    public static int dp2px(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5);
    }

    public static void ActionBarDrawerToggleAnimation(ActionBarDrawerToggle toggle, boolean changeBackUpState) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(changeBackUpState ? 0 : 1, changeBackUpState ? 1 : 0);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            toggle.onDrawerSlide(null, value);
        });
        valueAnimator.setDuration(250);
        valueAnimator.start();
    }
}
