package com.TyxApp.bangumi.player.cover;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.player.VideoPlayerEvent;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class DanmakuCover extends BaseCover {
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private ViewStub mViewStub;
    private BaseDanmakuParser mParser;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener = (sharedPreferences, key) -> {
        if (key.equals(getContext().getString(R.string.key_danmaku_textsize))) {
            if (mDanmakuContext != null) {
                float textSize = sharedPreferences.getInt(getContext().getString(R.string.key_danmaku_textsize), 70);
                mDanmakuContext.setScaleTextSize(textSize / 100.0f);
            }
        } else if (key.equals(getContext().getString(R.string.key_danmaku_maxline))) {
            if (mDanmakuContext != null) {
                int maxLine = sharedPreferences.getInt(getContext().getString(R.string.key_danmaku_maxline), 3);
                HashMap<Integer, Integer> maxLInesPair = new HashMap<>(16);
                maxLInesPair.put(BaseDanmaku.TYPE_SCROLL_RL, maxLine);
                mDanmakuContext.setMaximumLines(maxLInesPair);
            }
        }
    };

    private IReceiverGroup.OnGroupValueUpdateListener mGroupValueUpdateListener = new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{
                    VideoPlayerEvent.Key.DANMAKU_VISIBLE
            };
        }

        @Override
        public void onValueUpdate(String key, Object value) {
            if ((boolean) value) {
                if (mDanmakuView != null && mDanmakuView.isPrepared() && !mDanmakuView.isShown()) {
                    mDanmakuView.show();
                }
            } else {
                if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isShown()) {
                    mDanmakuView.hide();
                }
            }
        }
    };

    public DanmakuCover(Context context) {
        super(context);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_viewstub, null);
        view.setBackgroundColor(Color.TRANSPARENT);
        mViewStub = view.findViewById(R.id.ViewStub);
        return view;
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        getGroupValue().registerOnGroupValueUpdateListener(mGroupValueUpdateListener);
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        getGroupValue().unregisterOnGroupValueUpdateListener(mGroupValueUpdateListener);
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_START) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.start();
                if (mDanmakuView.isShown()) {
                    getGroupValue().putBoolean(VideoPlayerEvent.Key.DANMAKU_VISIBLE, true);
                }
            }
        } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.pause();
            }
        } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_RESUME) {
            if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
                mDanmakuView.resume();
            }
        } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.seekTo((long) bundle.getInt(EventKey.INT_DATA));
            }
        } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.release();
            }
        } else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_STOP) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.stop();
            }
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    public void setParser(BaseDanmakuParser danmakuParser) {
        if (mDanmakuView == null) {
            initDanmakuView();
        }
        mDanmakuView.setCallback(new CallBack() {
            @Override
            public void prepared() {
                notifyReceiverEvent(VideoPlayerEvent.Code.CODE_DANMAKU_PREPARED, null);
                boolean isShow = PreferenceUtil.getBollean(getContext().getString(R.string.key_danmaku_switch), true);
                if (!isShow && mDanmakuView.isShown()) {
                    hideDanmaku();
                }
            }
        });
        if (mDanmakuView.isPrepared()) {
            if (mParser != null) {
                mParser.release();
            }
        }
        mParser = danmakuParser;
        mDanmakuView.prepare(danmakuParser, mDanmakuContext);
    }

    private void initDanmakuView() {
        mViewStub.setLayoutResource(R.layout.layout_danmakuview);
        mDanmakuView = mViewStub.inflate().findViewById(R.id.DanmakuView);
        mDanmakuContext = DanmakuContext.create();

        int maxLine = PreferenceUtil.getInt(getContext().getString(R.string.key_danmaku_maxline), 3);
        float textSize = PreferenceUtil.getInt(getContext().getString(R.string.key_danmaku_textsize), 70);

        HashMap<Integer, Integer> maxLInesPair = new HashMap<>(16);
        maxLInesPair.put(BaseDanmaku.TYPE_SCROLL_RL, maxLine);
        //设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>(16);
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        //创建弹幕上下文
        mDanmakuContext = DanmakuContext.create();
        //设置一些相关的配置
        mDanmakuContext.setDuplicateMergingEnabled(false)
                //是否重复合并
                .setScrollSpeedFactor(1.2f)
                //设置文字的比例
                .setScaleTextSize(textSize / 100.0f)
                //设置显示最大行数
                .setMaximumLines(maxLInesPair)
                //设置防，null代表可以重叠
                .preventOverlapping(overlappingEnablePair);
    }

    private void showDanmaku() {
        mDanmakuView.show();
        getGroupValue().putBoolean(VideoPlayerEvent.Key.DANMAKU_VISIBLE, true);
    }

    private void hideDanmaku() {
        mDanmakuView.hide();
        getGroupValue().putBoolean(VideoPlayerEvent.Key.DANMAKU_VISIBLE, false);
    }

    class CallBack implements DrawHandler.Callback {

        @Override
        public void prepared() {

        }

        @Override
        public void updateTimer(DanmakuTimer timer) {

        }

        @Override
        public void danmakuShown(BaseDanmaku danmaku) {

        }

        @Override
        public void drawingFinished() {

        }
    }
}
