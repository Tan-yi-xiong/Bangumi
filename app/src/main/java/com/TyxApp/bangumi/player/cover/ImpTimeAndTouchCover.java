package com.TyxApp.bangumi.player.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.kk.taurus.playerbase.player.OnTimerUpdateListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;

public abstract class ImpTimeAndTouchCover extends BaseCover implements OnTimerUpdateListener, OnTouchGestureListener {

    public ImpTimeAndTouchCover(Context context) {
        super(context);
    }

    @Override
    public void onSingleTapUp(MotionEvent event) {

    }

    @Override
    public void onDoubleTap(MotionEvent event) {

    }

    @Override
    public void onDown(MotionEvent event) {

    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    @Override
    public void onEndGesture() {

    }
}
