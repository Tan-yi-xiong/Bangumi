package com.TyxApp.bangumi.player.cover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.player.bottomsheet.MainBottomSheet;
import com.TyxApp.bangumi.player.bottomsheet.VideoSpeedBottomSheet;
import com.TyxApp.bangumi.player.VideoPlayerEvent;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PlayerControlCover extends ImpTimeAndTouchListenerCover {
    @BindView(R.id.video_player_back)
    ImageButton videoPlayerBackButtom;
    @BindView(R.id.video_player_more)
    ImageButton videoPlayerMoreButtom;
    @BindView(R.id.video_player_title)
    TextView videoPlayerTitle;
    @BindView(R.id.player_control_seekBar)
    SeekBar playerSeekBar;
    @BindView(R.id.player_puase_start_control)
    ImageButton playerPuaseStartButton;
    @BindView(R.id.player_control_full_screen)
    ImageButton fullScrenButton;
    @BindView(R.id.tv_current_time)
    TextView currentTime;
    @BindView(R.id.tv_total_time)
    TextView durationTime;
    @BindView(R.id.player_control_next)
    ImageButton nextButton;
    @BindView(R.id.time)
    TextView timeTextView;
    @BindView(R.id.topLayout)
    View topView;

    private Unbinder mUnbinder;
    private SimpleDateFormat mDateFormat;

    private static final int SINGLE_TAP = 0;
    private static final int HIND_VIEW = 1;

    private Bundle mBundle = new Bundle();

    @SuppressLint("HandlerLeak")
    private Handler tapEventHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case SINGLE_TAP:
                    boolean isVisble = getView().getVisibility() == View.VISIBLE;
                    if (isVisble) {
                        AnimationUtil.fadeOut(getView(), 250);
                        mBundle.putBoolean(VideoPlayerEvent.Key.CONTROL_VIEW_SHOW, false);
                        notifyReceiverEvent(VideoPlayerEvent.Code.CODE_CONTROL_VIEW_SHOW, mBundle);
                    } else {
                        timeTextView.setText(mDateFormat.format(System.currentTimeMillis()));
                        AnimationUtil.fadeIn(getView(), 250);
                        mBundle.putBoolean(VideoPlayerEvent.Key.CONTROL_VIEW_SHOW, true);
                        notifyReceiverEvent(VideoPlayerEvent.Code.CODE_CONTROL_VIEW_SHOW, mBundle);
                    }
                    break;

                case HIND_VIEW:
                    if (getView().getVisibility() == View.VISIBLE) {
                        AnimationUtil.fadeOut(getView(), 250);
                        mBundle.putBoolean(VideoPlayerEvent.Key.CONTROL_VIEW_SHOW, false);
                        notifyReceiverEvent(VideoPlayerEvent.Code.CODE_CONTROL_VIEW_SHOW, mBundle);
                    }
                    break;
            }
        }
    };

    public PlayerControlCover(Context context) {
        super(context);
    }

    @Override
    public void onSingleTapUp(MotionEvent event) {
        //保证一次点击只处理一次事件
        tapEventHandle.removeMessages(SINGLE_TAP);
        tapEventHandle.removeMessages(HIND_VIEW);

        tapEventHandle.sendEmptyMessageDelayed(SINGLE_TAP, 130);//130ms后双击事件没触发就触发单击事件
        tapEventHandle.sendEmptyMessageDelayed(HIND_VIEW, 5000);//5s后隐藏View
    }

    @Override
    public void onDoubleTap(MotionEvent event) {
        //按下两次为暂停事件, 所以取消单击事件
        tapEventHandle.removeMessages(SINGLE_TAP);
    }

    @Override
    public void onReceiverBind() {
        mDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mUnbinder = ButterKnife.bind(this, getView());
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Bundle bundle = new Bundle();
                bundle.putInt(EventKey.INT_DATA, seekBar.getProgress());
                requestSeek(bundle);
            }
        });
        getGroupValue().registerOnGroupValueUpdateListener(new IReceiverGroup.OnGroupValueUpdateListener() {
            @Override
            public String[] filterKeys() {
                return new String[] {
                        VideoPlayerEvent.Key.IS_FULLSCREEN_KEY
                };
            }

            @Override
            public void onValueUpdate(String key, Object value) {
                if (VideoPlayerEvent.Key.IS_FULLSCREEN_KEY.equals(key)) {
                    if ((boolean) value) {
                        topView.setVisibility(View.VISIBLE);
                        fullScrenButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);
                    } else {
                        topView.setVisibility(View.GONE);
                        fullScrenButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        super.onReceiverBind();
    }

    @OnClick(value = {
            R.id.player_control_next,
            R.id.video_player_more,
            R.id.player_control_full_screen,
            R.id.player_puase_start_control,
            R.id.video_player_back})
    public void onClick(View view) {
        //点击按钮后重新计时隐藏View;
        tapEventHandle.removeMessages(HIND_VIEW);
        tapEventHandle.sendEmptyMessageDelayed(HIND_VIEW, 5000);

        switch (view.getId()) {
            case R.id.player_puase_start_control:
                if (playerPuaseStartButton.isSelected()) {
                    requestResume(null);
                } else {
                    requestPause(null);
                }
                break;

            case R.id.video_player_back:
                notifyReceiverEvent(VideoPlayerEvent.Code.CODE_BACK, null);
                break;

            case R.id.player_control_full_screen:
                notifyReceiverEvent(VideoPlayerEvent.Code.CODE_FULL_SCREEN, null);
                break;

            case R.id.video_player_more:
                notifyReceiverEvent(VideoPlayerEvent.Code.CODE_PLAYER_MORE_CLICK, null);
                break;

            case R.id.player_control_next:
                notifyReceiverEvent(VideoPlayerEvent.Code.CODE_NEXT, null);
                break;
        }
    }


    @Override
    protected View onCreateCoverView(Context context) {
        return LayoutInflater.from(context).
                inflate(R.layout.layout_cover_control, null);
    }

    @Override
    protected void onCoverDetachedToWindow() {
        tapEventHandle.removeCallbacksAndMessages(null);
        super.onCoverDetachedToWindow();
    }

    @Override
    public void onTimerUpdate(int curr, int duration, int bufferPercentage) {
        setSeekBar(curr, duration, bufferPercentage);
        setTimeText(curr, duration);
    }

    @Override
    public void onReceiverUnBind() {
        mUnbinder.unbind();
        super.onReceiverUnBind();
    }

    private void setTimeText(int curr, int duration) {
        String currText = TimeUtil.getTimeSmartFormat(curr);
        String durationText = TimeUtil.getTimeSmartFormat(duration);
        currentTime.setText(currText);
        durationTime.setText(durationText);
    }

    private void setSeekBar(int curr, int duration, int bufferPercentage) {
        playerSeekBar.setMax(duration);
        playerSeekBar.setProgress(curr);
        float seekBarBuffer = bufferPercentage * 1.0f / 100 * duration;
        playerSeekBar.setSecondaryProgress((int) seekBarBuffer);
    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        if (eventCode == VideoPlayerEvent.Code.CODE_SYNC_PLAY_TIME) {
            long playTime = bundle.getLong(EventKey.LONG_DATA);
            currentTime.setText(TimeUtil.getTimeSmartFormat(playTime));
        }
        return super.onPrivateEvent(eventCode, bundle);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {

            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                DataSource dataSource = (DataSource) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                setTitle(dataSource);
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE:
                int state = bundle.getInt(EventKey.INT_DATA);
                if (state == IPlayer.STATE_PAUSED) {
                    playerPuaseStartButton.setSelected(true);
                } else if (state == IPlayer.STATE_STARTED) {
                    playerPuaseStartButton.setSelected(false);
                }
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                boolean isNext = PreferenceUtil.getBollean(getContext().getString(R.string.key_auto_play_next), true);
                if (isNext) {
                    notifyReceiverEvent(VideoPlayerEvent.Code.CODE_NEXT, null);
                }
                break;
        }
    }

    private void setTitle(DataSource dataSource) {
        if (dataSource != null) {
            videoPlayerTitle.setText(dataSource.getTitle());
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }
}
