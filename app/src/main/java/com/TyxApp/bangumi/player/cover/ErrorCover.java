package com.TyxApp.bangumi.player.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.player.VideoPlayerEvent;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.kk.taurus.playerbase.assist.InterKey;
import com.kk.taurus.playerbase.config.PConst;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ErrorCover extends BaseCover {

    @BindView(R.id.error_tips)
    TextView errorTextView;
    @BindView(R.id.btn_retry)
    Button retryButton;
    private Unbinder mUnbinder;

    private static final int STATE_ERROR = -1;
    private static final int STATE_MOBILE = 1;
    private int mState;

    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[] {
                            VideoPlayerEvent.Key.NOTIFI_ERROR_COVER_SHOW};
                }

                @Override
                public void onValueUpdate(String key, Object value) {
                    handleUI(NetworkUtils.getNetworkState(getContext()));
                }
            };

    public ErrorCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        mUnbinder.unbind();
        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mUnbinder = ButterKnife.bind(this, getView());
        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
//        handleUI(NetworkUtils.getNetworkState(getContext()));
    }

    @Override
    public void onProducerData(String key, Object data) {
        if (key.equals(InterKey.KEY_NETWORK_STATE)) {
            Toast.makeText(getContext(), "您正在使用移动网络喔~", Toast.LENGTH_SHORT).show();
        }
        super.onProducerData(key, data);
    }

    private void handleUI(int networkState) {
        if (networkState == PConst.NETWORK_STATE_WIFI) {
            if (getView().getVisibility() == View.VISIBLE) {
                hideUI();
            }
        } else if (networkState < 0) {
            mState = STATE_ERROR;
            showUI();
        } else {
            boolean playInMobile = PreferenceUtil.getBollean(getContext().getString(R.string.key_play_no_wifi), false);
            mState = STATE_MOBILE;
            if (!playInMobile) {
               showUI();
            }
        }
    }

    private void showUI() {
        String errorText;
        String buttonText;
        if (mState == STATE_ERROR) {
            errorText = "出错了!!!";
            buttonText = "重试";
        } else {
            errorText = "您正在使用移动网络!";
            buttonText = "继续";
        }
        getView().setVisibility(View.VISIBLE);
        errorTextView.setText(errorText);
        retryButton.setText(buttonText);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EventKey.BOOL_DATA, true);
        notifyReceiverEvent(VideoPlayerEvent.Code.CODE_ERROR_COVER_SHOW, bundle);
    }

    @OnClick(R.id.btn_retry)
    public void onClick(View view) {
        if (mState == STATE_ERROR) {
            requestRetry(null);
        } else {
            requestResume(null);
        }
        hideUI();
    }

    private void hideUI() {
        getView().setVisibility(View.GONE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EventKey.BOOL_DATA, false);
        notifyReceiverEvent(VideoPlayerEvent.Code.CODE_ERROR_COVER_SHOW, bundle);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.layout_cover_error, null);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET) {
            handleUI(NetworkUtils.getNetworkState(getContext()));
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        mState = STATE_ERROR;
        showUI();
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public int getCoverLevel() {
        return levelHigh(0);
    }
}
