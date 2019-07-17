package com.TyxApp.bangumi.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.util.LogUtil;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

public class VideoLoadingView extends LinearLayout {
    private GradientDrawable dot;
    private ObjectAnimator[] mDotAnimators;
    private ImageView[] dots;
    private boolean onLayoutReach;

    private static final int DURATION = 500;
    private static final int DOT_SIZE = 3;
    private static final int POST_DEV = 6;

    public VideoLoadingView(Context context) {
        this(context, null);
    }

    public VideoLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        removeAllViews();
        dot = new GradientDrawable();
        dots = new ImageView[3];
        setBackgroundColor(Color.TRANSPARENT);

        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        dot.setSize(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        for (int i = 0; i < DOT_SIZE; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(dot);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.addView(imageView);
            addView(linearLayout);
            dots[i] = imageView;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!onLayoutReach) {
            onLayoutReach = true;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getWidth() / 5, getWidth() / 5);
            for (int i = 0; i < DOT_SIZE; i++) {
                dots[i].setLayoutParams(layoutParams);
            }
            if (getVisibility() == VISIBLE) {
                startAction();
            }

        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            startAction();
        } else {
            stopAction();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAction();
    }

    private void stopAction() {
        if (mDotAnimators == null) {
            return;
        }
        for (int i = 0; i < DOT_SIZE; i++) {
            ObjectAnimator animator = mDotAnimators[i];
            if (animator.isRunning()) {
                animator.removeAllListeners();
                animator.cancel();
                animator.end();
            }
        }
    }

    private void startAction() {
        if (mDotAnimators == null) {
            mDotAnimators = new ObjectAnimator[DOT_SIZE];
        }
        for (int i = 0; i < DOT_SIZE; i++) {
            ImageView imageView = dots[i];
            imageView.setTranslationY((float) (getHeight() / POST_DEV));
            PropertyValuesHolder holder = PropertyValuesHolder.ofFloat(
                    ImageView.TRANSLATION_Y,
                    (float) ((-getHeight()) / POST_DEV));

            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageView, holder);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setDuration(DURATION);
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            animator.setStartDelay(i * 166);
            animator.start();
            mDotAnimators[i] = animator;
        }
    }
}
