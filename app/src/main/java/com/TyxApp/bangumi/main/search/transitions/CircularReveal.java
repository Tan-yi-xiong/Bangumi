package com.TyxApp.bangumi.main.search.transitions;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.TyxApp.bangumi.util.LogUtil;


public class CircularReveal extends Visibility {
    private Point center;//动画开始点


    public CircularReveal() {
    }

    public CircularReveal(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (view == null || view.getId() == -1) {
            return null;
        }
        return ViewAnimationUtils.createCircularReveal(
                view,
                center == null ? view.getWidth() / 2 : center.x,
                center == null ? view.getWidth() / 2 : center.y,
                0,
               getFullyRevealedRadius(view)
        );
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (view == null || view.getId() == -1) {
            return null;
        }
        return ViewAnimationUtils.createCircularReveal(
                view,
                center == null ? view.getWidth() / 2 : center.x,
                center == null ? view.getWidth() / 2 : center.y,
                getFullyRevealedRadius(view),
                0
        );
    }

    private float getFullyRevealedRadius(View view) {
        return (float) Math.hypot(
                center == null ? view.getWidth() : center.x,
                center == null ? view.getHeight() : center.y);
    }
}
