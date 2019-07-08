package com.TyxApp.bangumi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class BannerView extends ViewPager {
    private long mDuration;
    private boolean bannerSwitch;
    private int bannerTime;
    private Handler bannerHandler;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        CoustomViewPagerScroller mScroller = new CoustomViewPagerScroller(context);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopLunbo();
                mDuration = 80;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (bannerSwitch) {
                    startLunbo(bannerTime);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int getmChildCount() {
        return getAdapter().getCount() - 2;
    }


    @SuppressLint("HandlerLeak")
    public void startLunbo(int time) {
        bannerSwitch = true;
        bannerTime = time;
        if (bannerHandler == null) {
            addOnPageChangeListener(new MOnPageChangeListener() {
                @Override
                public void onPageScrollStateChanged(int state) {
                    if (SCROLL_STATE_IDLE == state) {
                        if (getCurrentItem() == 0) {
                            setCurrentItem(getmChildCount(), false);
                        } else if(getCurrentItem() == getmChildCount() + 1) {
                            setCurrentItem(1, false);
                        }
                    }
                }
            });
            setCurrentItem(1);
            bannerHandler = new Handler() {
                public void handleMessage(Message mess) {
                    mDuration = 200;
                    setCurrentItem(getCurrentItem() + 1);
                    sendEmptyMessageDelayed(0, bannerTime);
                }
            };
        }
        bannerHandler.removeCallbacksAndMessages(null);
        bannerHandler.sendEmptyMessageDelayed(0, time);
    }

    public void startLunbo() {
        int time;
        if (bannerTime == 0) {
            time = 2000;
        } else {
            time = bannerTime;
        }
        startLunbo(time);
    }

    public void stopLunbo() {
        if (bannerHandler == null)
            return;
        bannerHandler.removeCallbacksAndMessages(null);
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public static class MOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 自定义Scroller控制轮播图速度
     *
     */
    class CoustomViewPagerScroller extends Scroller {

        public CoustomViewPagerScroller(Context context) {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, (int) mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) mDuration);
        }
    }
}
