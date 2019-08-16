package com.TyxApp.bangumi.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;

import androidx.appcompat.app.ActionBarDrawerToggle;

public class AnimationUtil {
    public static int dp2px(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5);
    }

    public static void fadeIn(View view, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
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
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    public static void shrink(View view) {
        if (view.getHeight() == 0 || view.getWidth() == 0) {
            return;
        }
        int measureHeight = view.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofInt(measureHeight, 0);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        long duration = (long) ((float)measureHeight / view.getContext().getResources().getDisplayMetrics().density);
        animator.setDuration(duration);
        animator.start();
    }

    public static void unfold(View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int measureHeight = view.getMeasuredHeight();
        if (measureHeight == 0) {
            return;
        }
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(0, measureHeight);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        long duration = (long) ((float)measureHeight / view.getContext().getResources().getDisplayMetrics().density);
        animator.setDuration(duration);
        animator.start();
    }

    public static void unfoldIncrease(View view) {
        int formerHeight = view.getHeight();
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int measureHeight = view.getMeasuredHeight();
        if (measureHeight == formerHeight) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(formerHeight, measureHeight);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.setDuration(100);
        animator.start();
    }

    public static void popAnima(View view) {
        if (view == null || view.getVisibility() == View.GONE) {
            return;
        }
        view.setAlpha(0f);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setInterpolator(AnimationUtils.loadInterpolator(view.getContext(), android.R.interpolator.overshoot))
                .setStartDelay(150);
    }

    public static class AnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
