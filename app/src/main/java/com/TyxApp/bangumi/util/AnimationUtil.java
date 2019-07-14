package com.TyxApp.bangumi.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

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

    public static void fadeIn(View view, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

    public static void fadeOut(View view, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0);
        animator.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }
}
