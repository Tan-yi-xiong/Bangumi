package com.TyxApp.bangumi.player.cover;

import android.content.Context;
import android.view.MotionEvent;

import com.TyxApp.bangumi.util.LogUtil;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;

public abstract class ImpTouchListenerCover extends BaseCover implements OnTouchGestureListener {
    public ImpTouchListenerCover(Context context) {
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
        onScroll(e1, e2);
    }

    public void onScroll(MotionEvent downPoint, MotionEvent endPoint) {

    }

    @Override
    public void onEndGesture() {

    }
}
