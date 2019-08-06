package com.TyxApp.bangumi.player;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.TyxApp.bangumi.player.cover.ErrorCover;
import com.TyxApp.bangumi.player.cover.GestureCover;
import com.TyxApp.bangumi.player.cover.PlayerControlCover;
import com.TyxApp.bangumi.player.cover.VideoPlayerEvent;
import com.TyxApp.bangumi.util.LogUtil;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

public class LocalPlayerFragment extends Fragment {
    private BaseVideoView mVideoView;
    private ReceiverGroup mReceiverGroup;
    private boolean isUserPause;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVideoView = new BaseVideoView(requireContext());
        mVideoView.setBackgroundColor(Color.BLACK);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoView.setLayoutParams(params);
        initVideoView();


        return mVideoView;
    }

    private OnVideoViewEventHandler mVideoViewEventHandler =
            new OnVideoViewEventHandler() {
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
                    super.onAssistHandle(assist, eventCode, bundle);
                    coverEventHandle(assist, eventCode, bundle);
                }
            };

    private void coverEventHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
        switch (eventCode) {
            case VideoPlayerEvent.Code.CODE_BACK://返回图标按下
                requireActivity().finish();
                break;

            case VideoPlayerEvent.Code.CODE_SPEED_CHANGE://速度调节
                float speed = bundle.getFloat(VideoPlayerEvent.Key.SPEED_DATA_KEY);
                assist.setSpeed(speed);
                break;

            case VideoPlayerEvent.Code.CODE_ERROR_COVER_SHOW:
                boolean isShow = bundle.getBoolean(EventKey.BOOL_DATA);
                if (isShow) {
                    mVideoView.getSuperContainer().setGestureEnable(false);
                } else {
                    mVideoView.getSuperContainer().setGestureEnable(true);
                }
                break;
        }
    }

    private void initVideoView() {
        mReceiverGroup = new ReceiverGroup();
        mReceiverGroup.addReceiver(PlayerControlCover.class.getName(), new PlayerControlCover(requireContext(), getChildFragmentManager()));
        mReceiverGroup.addReceiver(GestureCover.class.getName(), new GestureCover(requireActivity()));
        mVideoView.setReceiverGroup(mReceiverGroup);

        mVideoView.setEventHandler(mVideoViewEventHandler);

        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, true, true);

        String filePath = requireActivity().getIntent().getStringExtra(PlayerActivity.INTENT_KEY);
        DataSource dataSource = new DataSource(filePath);
        dataSource.setTitle(filePath.substring(filePath.lastIndexOf("/") + 1));
        mVideoView.setDataSource(dataSource);
    }

    public static LocalPlayerFragment newInstance() {
        return new LocalPlayerFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!mVideoView.isInPlaybackState()) {
            mVideoView.start();
        } else if (!isUserPause) {
            mVideoView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVideoView.stopPlayback();
    }
}
