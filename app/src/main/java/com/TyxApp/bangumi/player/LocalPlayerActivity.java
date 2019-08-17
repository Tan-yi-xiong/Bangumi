package com.TyxApp.bangumi.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.player.bottomsheet.MainBottomSheet;
import com.TyxApp.bangumi.player.bottomsheet.VideoSpeedBottomSheet;
import com.TyxApp.bangumi.player.cover.ErrorCover;
import com.TyxApp.bangumi.player.cover.GestureCover;
import com.TyxApp.bangumi.player.cover.LoadingCover;
import com.TyxApp.bangumi.player.cover.PlayerControlCover;
import com.TyxApp.bangumi.util.LogUtil;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

public class LocalPlayerActivity extends AppCompatActivity {
    private BaseVideoView mBaseVideoView;
    public static final String INTENT_KEY = "I_K";
    private boolean isUserPause;
    private SensorEventListener mSensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_player);
        mBaseVideoView = findViewById(R.id.videoView);
        ReceiverGroup receiverGroup = new ReceiverGroup();
        receiverGroup.addReceiver(PlayerControlCover.class.getName(), new PlayerControlCover(this));
        receiverGroup.addReceiver(LoadingCover.class.getName(), new LoadingCover(this));
        receiverGroup.addReceiver(GestureCover.class.getName(), new GestureCover(this));
        mBaseVideoView.setReceiverGroup(receiverGroup);
        mBaseVideoView.setEventHandler(new OnVideoViewEventHandler() {
            @Override
            public void requestPause(BaseVideoView videoView, Bundle bundle) {
                super.requestPause(videoView, bundle);
                isUserPause = true;
            }

            @Override
            public void requestResume(BaseVideoView videoView, Bundle bundle) {
                super.requestResume(videoView, bundle);
                isUserPause = false;
            }

            @Override
            public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
                if (eventCode == VideoPlayerEvent.Code.CODE_BACK) {//返回图标按下
                    finish();
                } else if (eventCode == VideoPlayerEvent.Code.CODE_PLAYER_MORE_CLICK) {
                    showMainBottomSheet();
                }

            }

        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        receiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, true, true);
        hindStateBar();
    }

    private void showMainBottomSheet() {
        String[] itemTexts = getResources().getStringArray(R.array.main_bottomsheet_item);
        MainBottomSheet mainBottomSheet = MainBottomSheet.newInstance(itemTexts);
        mainBottomSheet.setOnItemClickListener(pos -> {
            mainBottomSheet.dismiss();
            switch (pos) {
                case 0://重播
                    mBaseVideoView.rePlay(0);
                    break;
                case 1://调速
                    showVideoSpeedBottomSheet();
                    break;
                case 2://下载
                    Toast.makeText(this, "此视频已下载", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        mainBottomSheet.show(getSupportFragmentManager(), MainBottomSheet.class.getName());
    }

    private void showVideoSpeedBottomSheet() {
        String[] speedTexts = getResources().getStringArray(R.array.video_speed_bottomsheet_item);
        VideoSpeedBottomSheet speedBottomSheet = VideoSpeedBottomSheet.newInstance(speedTexts);
        speedBottomSheet.setOnItemClickListener(pos -> {
            switch (pos) {
                case 0:
                    mBaseVideoView.setSpeed(0.5f);
                    break;
                case 1:
                    mBaseVideoView.setSpeed(0.7f);
                    break;
                case 3:
                    mBaseVideoView.setSpeed(1.5f);
                    break;
                case 4:
                    mBaseVideoView.setSpeed(2.0f);
                    break;
                default:
                    mBaseVideoView.setSpeed(1.0f);
            }
        });
        speedBottomSheet.show(getSupportFragmentManager(), VideoSpeedBottomSheet.class.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensor();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!mBaseVideoView.isInPlaybackState()) {
            String path = getIntent().getStringExtra(INTENT_KEY);
            DataSource dataSource = new DataSource(path);
            dataSource.setTitle(path.substring(path.lastIndexOf("/") + 1));
            mBaseVideoView.setDataSource(dataSource);
            mBaseVideoView.start();
        } else if (!isUserPause) {
            mBaseVideoView.resume();
        }
    }

    private void registerSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravitySensor != null) {
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values[0] >= 8.5f) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else if (event.values[0] <= -8.5f) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            sensorManager.registerListener(mSensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    private void hindStateBar() {
        int uiFlage = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(uiFlage);
    }

    @Override
    protected void onPause() {
        unRegisterSensor();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mBaseVideoView.isInPlaybackState()) {
            mBaseVideoView.pause();
        }
        super.onPause();
    }

    private void unRegisterSensor() {
        if (mSensorEventListener != null) {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mSensorEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaseVideoView.stopPlayback();
    }

    public static void startLocalPlayerActivity(Context context, String path) {
        Intent intent = new Intent(context, LocalPlayerActivity.class);
        intent.putExtra(INTENT_KEY, path);
        context.startActivity(intent);
    }
}
