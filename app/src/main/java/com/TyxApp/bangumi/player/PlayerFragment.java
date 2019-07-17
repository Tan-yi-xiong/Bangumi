package com.TyxApp.bangumi.player;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.StackBangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;
import com.TyxApp.bangumi.data.VideoPlayerEvent;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.player.adapter.ContentAdapter;
import com.TyxApp.bangumi.player.cover.ErrorCover;
import com.TyxApp.bangumi.player.cover.GestureCover;
import com.TyxApp.bangumi.player.cover.LoadingCover;
import com.TyxApp.bangumi.player.cover.PlayerControlCover;
import com.TyxApp.bangumi.util.LogUtil;
import com.google.android.material.snackbar.Snackbar;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.config.PConst;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.utils.NetworkUtils;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.util.ArrayList;
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
    private List<StackBangumi> mStackBangumis;//存放点击了的更多番剧, 模拟一个栈。
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
                public void requestRetry(BaseVideoView videoView, Bundle bundle) {
                    LoadingPageData();
                    super.requestRetry(videoView, bundle);
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
                            if (isFullScreen()) {
                                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            } else {
                                requireActivity().finish();
                            }
                            break;

                        case VideoPlayerEvent.Code.CODE_SPEED_CHANGE:
                            float speed = bundle.getFloat(VideoPlayerEvent.Key.SPEED_DATA_KEY);
                            assist.setSpeed(speed);
                            break;

                        case VideoPlayerEvent.Code.CODE_DOWNLOAD:

                            break;

                        case VideoPlayerEvent.Code.CODE_BRIGHTNESS_ADJUST:
                            int brightness = bundle.getInt(EventKey.INT_DATA);
                            Window window = requireActivity().getWindow();
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.screenBrightness = brightness / 255.0f;
                            window.setAttributes(params);
                            break;

                        case VideoPlayerEvent.Code.CODE_NEXT:
                            if (currentJi == mBangumi.getJitotal() - 1) {
                                Toast.makeText(getContext(), "已经是最后一集啦", Toast.LENGTH_SHORT).show();
                            } else {
                                currentJi++;
                                mPlayerPresenter.getPlayerUrl(mBangumi.getVod_id(), currentJi);
                                mContentAdapter.setJiSelect(currentJi);
                            }
                            break;

                        case VideoPlayerEvent.Code.CODE_ERROR_COVER_SHOW:
                            boolean isShow = bundle.getBoolean(EventKey.BOOL_DATA);
                            if (isShow) {
                                mPlayerVideoview.getSuperContainer().setGestureEnable(false);
                            } else {
                                mPlayerVideoview.getSuperContainer().setGestureEnable(true);
                            }
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
            if (mStackBangumis.size() == 1) {
                requireActivity().finish();
            } else {
                mStackBangumis.remove(mStackBangumis.size() - 1);
                StackBangumi stackBangumi = mStackBangumis.get(mStackBangumis.size() - 1);
                mBangumi = stackBangumi.getBangumi();
                currentJi = stackBangumi.getPlayedJi();
                LoadingPageData();
            }

        }
    }

    @Override
    protected void initView() {
        mStackBangumis = new ArrayList<>();
        mStackBangumis.add(new StackBangumi(currentJi, mBangumi));

        mContentAdapter = new ContentAdapter(mBangumi, requireContext());
        mContentAdapter.setOnItemSelectListener(new ContentAdapter.OnItemSelectListener() {
            @Override
            public void onJiSelect(int pos) {
                mPlayerPresenter.getPlayerUrl(mBangumi.getVod_id(), pos);
                currentJi = pos;
            }

            @Override
            public void onBangumiSelect(Bangumi bangumi) {
                contentRecyclerView.scrollToPosition(0);
                addNewBangumiToList(bangumi);
                mBangumi = bangumi;
                currentJi = 0;
                LoadingPageData();
            }
        });
        contentRecyclerView.setAdapter(mContentAdapter);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        LoadingPageData();

        mReceiverGroup = new ReceiverGroup();
        mReceiverGroup.addReceiver(
                PlayerControlCover.class.getName(),
                new PlayerControlCover(requireContext(), getChildFragmentManager()));
        mReceiverGroup.addReceiver(LoadingCover.class.getName(), new LoadingCover(requireContext()));
        mReceiverGroup.addReceiver(GestureCover.class.getName(), new GestureCover(requireContext()));
        mReceiverGroup.addReceiver(ErrorCover.class.getName(), new ErrorCover(requireContext()));
        mPlayerVideoview.setReceiverGroup(mReceiverGroup);
        mPlayerVideoview.setEventHandler(mViewEventHandler);
        mPlayerVideoview.post(() -> videoViewPortraitHeighe = mPlayerVideoview.getHeight());

        requireActivity().getWindow()
                .setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_text_disabled_material_light));

        hindStateBar();
    }

    private void LoadingPageData() {
        mPlayerPresenter.getRecommendBangumis(mBangumi.getVod_id());
        mPlayerPresenter.getBangumiIntro(mBangumi.getVod_id());
        mPlayerPresenter.getBangumiJiList(mBangumi.getVod_id());
    }

    private void addNewBangumiToList(Bangumi bangumi) {
        mStackBangumis.get(mStackBangumis.size() - 1).setPlayedJi(currentJi);
        mStackBangumis.add(new StackBangumi(0, bangumi));
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
                .getBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, false);
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
                VideoPlayerEvent.Key.IS_FULLSCREEN_KEY,
                isLandscape,
                true);
        hindStateBar();
    }

    @Override
    public BasePresenter getPresenter() {
        mBangumi = requireActivity().getIntent().getParcelableExtra(PlayerActivity.INTENT_KEY);
        BaseBangumiParser parser = null;
        switch (mBangumi.getSoure()) {
            case BangumiPresistenceContract
                    .BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;
        }
        mPlayerPresenter = new PlayerPresenter(this, parser);
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
        mContentAdapter.notifiBangumiChange(mBangumi);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!isuserPuase) {
            mPlayerVideoview.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPlayerVideoview.pause();
    }

    @Override
    public void showBangumiJiList(List<TextItemSelectBean> jiList) {
        if (!jiList.isEmpty()) {
            jiList.get(currentJi).setSelect(true);
            mContentAdapter.notifijiListChange(jiList);
            mPlayerPresenter.getPlayerUrl(mBangumi.getVod_id(), currentJi);
            mBangumi.setJitotal(jiList.size());
        } else {
            Snackbar.make(contentRecyclerView, "解析失败", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void setPlayerUrl(String url) {
        DataSource dataSource = new DataSource(url);
        dataSource.setTitle(mBangumi.getName() + " 第" + (currentJi + 1) + "集");
        mPlayerVideoview.setDataSource(dataSource);
        int newState = NetworkUtils.getNetworkState(requireContext());
        if (newState == PConst.NETWORK_STATE_WIFI) {
            mPlayerVideoview.start();
        } else {
            mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.NOTIFI_ERROR_COVER_SHOW, true, true);
        }

    }

    @Override
    public void showRecommendBangumis(List<Bangumi> recommendBangumis) {
        mContentAdapter.notifiRecommendBangumisChange(recommendBangumis);
    }

    @Override
    public void showError(Throwable throwable) {
        int netState = NetworkUtils.getNetworkState(requireContext());
        String text;
        if (netState == -1) {
            text = "请联网后重试";
        } else {
            text = throwable.toString();
        }
        Snackbar.make(mPlayerVideoview, text, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onDestroyView() {
        mPlayerVideoview.stopPlayback();
        super.onDestroyView();
    }
}
