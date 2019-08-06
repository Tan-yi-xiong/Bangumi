package com.TyxApp.bangumi.player.cover;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.TyxApp.bangumi.R;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GestureCover extends ImpTouchListenerCover {
    @BindView(R.id.skip_text)
    TextView videoFastForwardTextView;
    private long fastForwardProgress;

    @BindView(R.id.pb_video_volume)
    ProgressBar videoVolumeProgressBar;
    @BindView(R.id.volume_progress_view)
    LinearLayout volumeProgressView;
    private AudioManager mAudioManager;
    private float startY;
    private int mSystemMaxVolume;
    private float mStep;

    @BindView(R.id.pb_video_brightness)
    ProgressBar videoBrightnessProgressBar;
    @BindView(R.id.brightness_progress_view)
    LinearLayout brightnessProgressView;
    private static final int MAX_BRIGHTNESS = 255;
    private int mBrightness;
    private int brightnessStepValue;
    private Bundle mBundle;

    private float screenWidth;

    private static final int SEEK_SCOPE = 30;

    private float stateBarHeight;
    private boolean isDownOnStateBarRegion;

    private int adjustState = -1;
    private static final int STATE_VIDEO_FAST_FORWARD = 0;
    private static final int STATE_VOLUME_ADJUST = 1;
    private static final int STATE_BRIGHTNESS_ADJUST = 2;
    private Unbinder mUnbinder;


    public GestureCover(Activity activity) {
        super(activity);
        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        float screenHeight = displayMetrics.heightPixels;

        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mSystemMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mStep = ((screenHeight / 3) / mSystemMaxVolume);
        brightnessStepValue = 255 / mSystemMaxVolume;

        ContentResolver resolver = activity.getContentResolver();
        mBrightness = Settings.System.getInt(resolver,
                Settings.System.SCREEN_BRIGHTNESS, 125);

        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        stateBarHeight = activity.getResources().getDimensionPixelSize(resId);

        mBundle = new Bundle();
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mUnbinder = ButterKnife.bind(this, getView());
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        mUnbinder.unbind();
    }

    @Override
    public void onDoubleTap(MotionEvent event) {
        PlayerStateGetter stateGetter = getPlayerStateGetter();
        if (stateGetter != null) {
            if (stateGetter.getState() == IPlayer.STATE_PAUSED) {
                requestResume(null);
            } else {
                requestPause(null);
            }
        }
    }

    @Override
    public void onDown(MotionEvent event) {
        startY = event.getY();
        //如果按下的点在状态栏区域默认用户想拉下状态栏, 滚动事件不响应
        isDownOnStateBarRegion = startY < stateBarHeight + 10;
    }

    @Override
    public void onScroll(MotionEvent downPoint, MotionEvent endPoint) {
        float distanceX = endPoint.getX() - downPoint.getX();
        float distanceY = endPoint.getY() - downPoint.getY();
        if (Math.abs(distanceX) < 15 && Math.abs(distanceY) < 15 || isDownOnStateBarRegion) {
            return;
        }
        //X滑动距离大于Y表示调节进度
        if (Math.abs(distanceX) > Math.abs(distanceY)) {
            if (adjustState == -1 || adjustState == STATE_VIDEO_FAST_FORWARD) {
                videoFastForward(distanceX);
                adjustState = STATE_VIDEO_FAST_FORWARD;
            }
        } else {
            //手指开始点放在屏幕右边表示调节音量
            if (downPoint.getX() > screenWidth / 2) {
                if (adjustState == -1 || adjustState == STATE_VOLUME_ADJUST) {
                    adjustState = STATE_VOLUME_ADJUST;
                    volumeAdjustment(endPoint);
                }
            } else {
                if (adjustState == -1 || adjustState == STATE_BRIGHTNESS_ADJUST) {
                    brightnessAdjustment(endPoint);
                    adjustState = STATE_BRIGHTNESS_ADJUST;
                }
            }
        }
    }

    private void videoFastForward(float distanceX) {
        float step = distanceX * SEEK_SCOPE;
        fastForwardProgress = (long) (getCurrentPosition() + step);
        mBundle.putLong(EventKey.LONG_DATA, fastForwardProgress);
        notifyReceiverPrivateEvent(PlayerControlCover.class.getName(),
                VideoPlayerEvent.Code.CODE_SYNC_PLAY_TIME, mBundle);

        String seekTimeText =
                TimeUtil.getTimeSmartFormat(fastForwardProgress) +
                        "/" +
                        TimeUtil.getTimeSmartFormat(getDuration());

        videoFastForwardTextView.setText(seekTimeText);
        if (videoFastForwardTextView.getVisibility() == View.GONE) {
            videoFastForwardTextView.setVisibility(View.VISIBLE);
        }
    }

    private int getCurrentPosition() {
        if (getPlayerStateGetter() != null)
            return getPlayerStateGetter().getCurrentPosition();
        return 0;
    }

    private int getDuration() {
        if (getPlayerStateGetter() != null)
            return getPlayerStateGetter().getDuration();
        return 0;
    }

    private void volumeAdjustment(MotionEvent event) {
        float distanceY = event.getY() - startY;
        if (mStep <= Math.abs(distanceY)) {
            startY = event.getY();
            if (distanceY < 0) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
            } else if (distanceY > 0) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
            }
        }
        int systemCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        videoVolumeProgressBar.setMax(mSystemMaxVolume);
        videoVolumeProgressBar.setProgress(systemCurrentVolume);
        if (volumeProgressView.getVisibility() == View.GONE) {
            volumeProgressView.setVisibility(View.VISIBLE);
        }
    }

    private void brightnessAdjustment(MotionEvent event) {
        float distanceY = event.getY() - startY;
        if (mStep <= Math.abs(distanceY)) {
            startY = event.getY();
            if (distanceY < 0) {
              mBrightness += brightnessStepValue;
                if (mBrightness > MAX_BRIGHTNESS) {
                    mBrightness = MAX_BRIGHTNESS;
                }
            } else if (distanceY > 0) {
                mBrightness -= brightnessStepValue;
                if (mBrightness < 0) {
                    mBrightness = 0;
                }
            }
            Window window = ((Activity) getContext()).getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.screenBrightness = mBrightness / 255.0f;
            window.setAttributes(params);
        }
        videoBrightnessProgressBar.setProgress(mBrightness);
        videoBrightnessProgressBar.setMax(MAX_BRIGHTNESS);
        if (brightnessProgressView.getVisibility() == View.GONE) {
            brightnessProgressView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEndGesture() {
        if (adjustState == STATE_VIDEO_FAST_FORWARD) {
            mBundle.putInt(EventKey.INT_DATA, (int) fastForwardProgress);
            requestSeek(mBundle);
            videoFastForwardTextView.setVisibility(View.GONE);
        } else if (adjustState == STATE_BRIGHTNESS_ADJUST) {
            brightnessProgressView.setVisibility(View.GONE);
        } else if (adjustState == STATE_VOLUME_ADJUST) {
            volumeProgressView.setVisibility(View.GONE);
        }
        //重置, 否则其他Cover触发onEndGesture框架也会调用这个方法
        adjustState = -1;
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.layout_cover_gesture, null);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }
}
