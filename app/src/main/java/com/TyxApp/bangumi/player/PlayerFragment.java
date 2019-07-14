package com.TyxApp.bangumi.player;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;
import com.TyxApp.bangumi.data.VideoPlayerEvent;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.player.adapter.ContentAdapter;
import com.TyxApp.bangumi.player.cover.ControlCover;
import com.google.android.material.snackbar.Snackbar;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class PlayerFragment extends BaseMvpFragment implements PlayContract.View {

    @BindView(R.id.player_videoview)
    BaseVideoView mPlayerVideoview;
    @BindView(R.id.rv_bangumi_relevant_information)
    RecyclerView contentRecyclerView;

    private ContentAdapter mContentAdapter;
    private PlayerPresenter mPlayerPresenter;
    private Bangumi mBangumi;
    private int videoViewPortraitHeighe;
    private ReceiverGroup mReceiverGroup;
    private int currentJi;
    private boolean isuserPuase;
    private OnVideoViewEventHandler mViewEventHandler =
            new OnVideoViewEventHandler() {
                @Override
                public void requestPause(BaseVideoView videoView, Bundle bundle) {
                    super.requestPause(videoView, bundle);
                    isuserPuase = true;
                    if (!isFullScreen()) {
                        showStateBar();
                    }
                }

                @Override
                public void requestResume(BaseVideoView videoView, Bundle bundle) {
                    super.requestResume(videoView, bundle);
                    isuserPuase = false;
                    if (!isFullScreen()) {
                        hindStateBar();
                    }
                }

                @Override
                public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
                    super.onAssistHandle(assist, eventCode, bundle);
                    switch (eventCode) {
                        case VideoPlayerEvent.Code.CODE_FULL_SCREEN:
                            requireActivity().setRequestedOrientation(isFullScreen() ?
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            break;

                        case VideoPlayerEvent.Code.CODE_BACK:
                            onBackPressed();
                            break;
                    }
                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        onBackPressed();
                    }
                });
    }

    private void onBackPressed() {
        if (isFullScreen()) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            requireActivity().finish();
        }
    }

    @Override
    protected void initView() {
        mBangumi = requireActivity().getIntent().getParcelableExtra(PlayerActivity.INTENT_KEY);
        mContentAdapter = new ContentAdapter(mBangumi, requireContext());
        mContentAdapter.setOnJiSelectListener((int pos) -> {
            mPlayerPresenter.getPlayerUrl(mBangumi.getVod_id(), pos);
            currentJi = pos;
        });
        contentRecyclerView.setAdapter(mContentAdapter);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        mPlayerPresenter.getRecommendBangumis(mBangumi.getVod_id());
        mPlayerPresenter.getBangumiIntro(mBangumi.getVod_id());
        mPlayerPresenter.getBangumiJiList(mBangumi.getVod_id());

        mReceiverGroup = new ReceiverGroup();
        mReceiverGroup.addReceiver(ControlCover.class.getName(), new ControlCover(requireContext()));
        mPlayerVideoview.setReceiverGroup(mReceiverGroup);
        mPlayerVideoview.setEventHandler(mViewEventHandler);
        mPlayerVideoview.post(() -> videoViewPortraitHeighe = mPlayerVideoview.getHeight());

        requireActivity().getWindow()
                .setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_text_disabled_material_light));

        hindStateBar();
    }

    private void hindStateBar() {
        int uiFlage = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (isFullScreen()) {
            uiFlage |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(uiFlage);
    }

    private boolean isFullScreen() {
        return mReceiverGroup.getGroupValue()
                .getBoolean(VideoPlayerEvent.Key.ISFULLSCREENKEY, false);
    }

    private void showStateBar() {
        final int UI_STATE = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(UI_STATE);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLandscape;
        int videoViewHeight;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            videoViewHeight = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            isLandscape = false;
            videoViewHeight = videoViewPortraitHeighe;
        }

        ViewGroup.LayoutParams layoutParams = mPlayerVideoview.getLayoutParams();
        layoutParams.height = videoViewHeight;
        mPlayerVideoview.requestLayout();

        mReceiverGroup.getGroupValue().putBoolean(
                VideoPlayerEvent.Key.ISFULLSCREENKEY,
                isLandscape,
                true);
        hindStateBar();
    }

    @Override
    public BasePresenter getPresenter() {
        mPlayerPresenter = new PlayerPresenter(this, new ZzzFun());
        return mPlayerPresenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_player;
    }

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public void showBangumiIntro(String intor) {
        mBangumi.setIntro(intor);
        mContentAdapter.notifiBangumiChange();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isuserPuase) {
            mPlayerVideoview.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerVideoview.pause();
    }

    @Override
    public void showBangumiJiList(List<TextItemSelectBean> jiList) {
        if (!jiList.isEmpty()) {
            jiList.get(0).setSelect(true);
        } else {
            Snackbar.make(contentRecyclerView, "解析失败", Snackbar.LENGTH_LONG).show();
        }
        mContentAdapter.notifijiListChange(jiList);
        mPlayerPresenter.getPlayerUrl(mBangumi.getVod_id(), 0);
    }

    @Override
    public void setPlayerUrl(String url) {
        DataSource dataSource = new DataSource(url);
        dataSource.setTitle(mBangumi.getName() + " " + mContentAdapter.getJiData(currentJi).getText());
        mPlayerVideoview.setDataSource(dataSource);
        mPlayerVideoview.start();
    }

    @Override
    public void showRecommendBangumis(List<Bangumi> recommendBangumis) {
        mContentAdapter.notifiRecommendBangumisChange(recommendBangumis);
    }

    @Override
    public void onDestroyView() {
        mPlayerVideoview.stopPlayback();
        super.onDestroyView();
    }
}
