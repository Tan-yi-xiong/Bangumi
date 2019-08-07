package com.TyxApp.bangumi.player;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpFragment;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.StackBangumi;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.BaseBangumiParser;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.Nico;
import com.TyxApp.bangumi.data.source.remote.Sakura;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.player.adapter.ContentAdapter;
import com.TyxApp.bangumi.player.cover.ErrorCover;
import com.TyxApp.bangumi.player.cover.GestureCover;
import com.TyxApp.bangumi.player.cover.LoadingCover;
import com.TyxApp.bangumi.player.cover.PlayerControlCover;
import com.TyxApp.bangumi.player.cover.VideoPlayerEvent;
import com.TyxApp.bangumi.server.DownloadBinder;
import com.TyxApp.bangumi.server.DownloadServer;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.util.LogUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.utils.NetworkUtils;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RemotePlayerFragment extends BaseMvpFragment implements PlayContract.View {

    @BindView(R.id.player_videoview)
    BaseVideoView mPlayerVideoview;
    @BindView(R.id.rv_bangumi_relevant_information)
    RecyclerView contentRecyclerView;
    @BindView(R.id.cover)
    ImageView cover;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.ji)
    TextView ji;
    @BindView(R.id.niandai)
    TextView niandai;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.cast)
    TextView cast;
    @BindView(R.id.staff)
    TextView staff;
    @BindView(R.id.intro)
    TextView intro;

    private ContentAdapter mContentAdapter;
    private Snackbar mSnackbar;
    private List<StackBangumi> mStackBangumiList;//存放点击了的更多番剧, 模拟一个栈。
    private PlayerPresenter mPlayerPresenter;
    private StackBangumi mStackBangumi;
    private int videoViewPortraitHeighe;
    private ReceiverGroup mReceiverGroup;
    private BottomSheetBehavior detailBottomSheet;

    private boolean isuserPuase;
    private boolean isFristLoading = true;

    private static final String SCREEN_STATE_KEY = "S_S_K";
    private static final String STACK_BANGUM_KEY = "S_B_K";

    private DownloadBinder mDownloadBinder;
    private ServiceConnection mServiceConnection;


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
            if (detailBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                detailBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else if (mStackBangumiList.size() == 1) {
                requireActivity().finish();
            } else {
                removeBangumiFromList();
            }
        }
    }

    private void removeBangumiFromList() {
        mStackBangumiList.remove(mStackBangumiList.size() - 1);
        mStackBangumi = mStackBangumiList.get(mStackBangumiList.size() - 1);
        LoadingPageData();
    }

    private void addBangumiToList(Bangumi bangumi) {
        StackBangumi stackBangumi = new StackBangumi(bangumi);
        bangumi.setHistoryTime(System.currentTimeMillis());
        mPlayerPresenter.setTime(bangumi);
        mStackBangumi = stackBangumi;
        mStackBangumiList.add(stackBangumi);
    }

    @Override
    public BasePresenter getPresenter() {
        Bangumi bangumi = requireActivity().getIntent().getParcelableExtra(PlayerActivity.INTENT_KEY);
        BaseBangumiParser parser = null;
        switch (bangumi.getVideoSoure()) {
            case BangumiPresistenceContract.BangumiSource.NiICO:
                parser = Nico.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.ZZZFUN:
                parser = ZzzFun.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.SAKURA:
                parser = Sakura.getInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.DILIDLI:
                parser = Dilidili.getInstance();
                break;
        }
        mPlayerPresenter = new PlayerPresenter(this, parser);
        return mPlayerPresenter;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mStackBangumiList = new ArrayList<>();
        if (savedInstanceState != null) {
            mStackBangumi = savedInstanceState.getParcelable(STACK_BANGUM_KEY);
            mPlayerVideoview.post(() -> {
                if (savedInstanceState != null) {
                    boolean isFullScreen = savedInstanceState.getBoolean(SCREEN_STATE_KEY, false);
                    mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, isFullScreen, true);
                    if (isFullScreen) {
                        Configuration configuration = new Configuration();
                        configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
                        onConfigurationChanged(configuration);
                    }
                }
            });
        }

        if (mStackBangumi == null) {
            Bangumi bangumi = requireActivity().getIntent().getParcelableExtra(PlayerActivity.INTENT_KEY);
            addBangumiToList(bangumi);
        } else {
            mStackBangumiList.add(mStackBangumi);
        }

        mContentAdapter = new ContentAdapter(mStackBangumi.getBangumi(), requireContext());
        mContentAdapter.setOnItemSelectListener(new ContentAdapter.OnItemSelectListener() {
            @Override
            public void onJiSelect(int pos) {
                if (mStackBangumi.getCurrentJi() == pos) {
                    return;
                }
                mPlayerPresenter.getPlayerUrl(mStackBangumi.getBangumiId(), pos);
                mStackBangumi.setCurrentJi(pos);
            }

            @Override
            public void onBangumiSelect(Bangumi bangumi) {
                addBangumiToList(bangumi);
                if (mPlayerVideoview.isInPlaybackState()) {
                    mPlayerVideoview.stop();
                }
                LoadingPageData();
            }

            @Override
            public void onFavoriteButtonClick(boolean isSelect) {
                mStackBangumi.getBangumi().setFavorite(!isSelect);
                mPlayerPresenter.setFavorite(mStackBangumi.getBangumi());
            }

            @Override
            public void onDetailClick() {
                detailBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        detailBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.detail_bottom_sheeet));

        contentRecyclerView.setAdapter(mContentAdapter);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));


        mReceiverGroup = new ReceiverGroup();
        mReceiverGroup.addReceiver(
                PlayerControlCover.class.getName(),
                new PlayerControlCover(requireContext(), getChildFragmentManager()));
        mReceiverGroup.addReceiver(LoadingCover.class.getName(), new LoadingCover(requireContext()));
        mReceiverGroup.addReceiver(GestureCover.class.getName(), new GestureCover(requireActivity()));
        mReceiverGroup.addReceiver(ErrorCover.class.getName(), new ErrorCover(requireContext()));
        mPlayerVideoview.setReceiverGroup(mReceiverGroup);
        mPlayerVideoview.setEventHandler(mViewEventHandler);
        mPlayerVideoview.post(() -> videoViewPortraitHeighe = mPlayerVideoview.getHeight());

        requireActivity().getWindow()
                .setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_text_disabled_material_light));

        hindStateBar();
    }


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
                    videoView.resume();
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
                    coverEventHandle(assist, eventCode, bundle);
                }
            };

    private void coverEventHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
        switch (eventCode) {
            case VideoPlayerEvent.Code.CODE_FULL_SCREEN://屏幕旋转
                if (isFullScreen()) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, !isFullScreen(), true);
                break;

            case VideoPlayerEvent.Code.CODE_BACK://返回图标按下
                if (isFullScreen()) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, !isFullScreen(), true);
                } else {
                    requireActivity().finish();
                }
                break;

            case VideoPlayerEvent.Code.CODE_SPEED_CHANGE://速度调节
                float speed = bundle.getFloat(VideoPlayerEvent.Key.SPEED_DATA_KEY);
                assist.setSpeed(speed);
                break;

            case VideoPlayerEvent.Code.CODE_DOWNLOAD://下载
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    downLoadVideo();
                }

                break;


            case VideoPlayerEvent.Code.CODE_NEXT://下一集
                if (mStackBangumi.isLastJi()) {
                    Toast.makeText(getContext(), "已经是最后一集啦", Toast.LENGTH_SHORT).show();
                } else {
                    mStackBangumi.nextJi();
                    mPlayerPresenter.getPlayerUrl(mStackBangumi.getBangumi().getVodId(), mStackBangumi.getCurrentJi());
                    mContentAdapter.setJiSelect(mStackBangumi.getCurrentJi());
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


    private void downLoadVideo() {
        if (TextUtils.isEmpty(mStackBangumi.getPlayingUrl()) || mStackBangumi.getPlayingUrl().contains(".html")
                || mStackBangumi.getBanhumiSourch().equals(BangumiPresistenceContract.BangumiSource.DILIDLI)) {
            Toast.makeText(requireContext(), "无法下载", Toast.LENGTH_SHORT).show();
            return;
        }
        mStackBangumi.getBangumi().setDownLoad(true);
        mPlayerPresenter.setDownload(mStackBangumi.getBangumi());
        if (mDownloadBinder != null) {
            String fileName = mContentAdapter.getJiList().get(mStackBangumi.getCurrentJi()).getText();
            mDownloadBinder.addTask(mStackBangumi.getBangumiId(), mStackBangumi.getBanhumiSourch(), mStackBangumi.getPlayingUrl(), fileName);
        } else {
            Intent intent = new Intent(requireActivity(), DownloadServer.class);
            requireActivity().startService(intent);
            if (mServiceConnection == null) {
                mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        mDownloadBinder = (DownloadBinder) service;
                        String fileName = mContentAdapter.getJiList().get(mStackBangumi.getCurrentJi()).getText();
                        mDownloadBinder.addTask(mStackBangumi.getBangumiId(), mStackBangumi.getBanhumiSourch(), mStackBangumi.getPlayingUrl(), fileName);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                };
            }
            requireActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void LoadingPageData() {
        //推荐为空才去获取
        if (mStackBangumi.getRecommendBangumis() == null) {
            mPlayerPresenter.getRecommendBangumis(mStackBangumi.getBangumiId());
        } else {
            showRecommendBangumis(mStackBangumi.getRecommendBangumis());
        }

        if (mStackBangumi.getPlayedJi() == null) {
            mPlayerPresenter.getBangumiJiList(mStackBangumi.getBangumiId());
        } else {
            showBangumiJiList(mStackBangumi.getPlayedJi());
        }
        if (mStackBangumi.getBangumi().getBangumiInfo() == null) {
            mPlayerPresenter.getBangumiInfo(mStackBangumi.getBangumiId());
        } else {
            showBangumiInfo(mStackBangumi.getBangumi().getBangumiInfo());
        }

        mPlayerPresenter.isFavorite(mStackBangumi.getBangumiId(), mStackBangumi.getBanhumiSourch());
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

        hindStateBar();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_player_remote;
    }

    public static RemotePlayerFragment newInstance() {
        return new RemotePlayerFragment();
    }

    @Override
    public void showBangumiInfo(BangumiInfo info) {
        mStackBangumi.getBangumi().setBangumiInfo(info);
        setBangumiInfo(info);
        mContentAdapter.notifiBangumiChange(mStackBangumi.getBangumi());
    }

    private void setBangumiInfo(BangumiInfo info) {
        Glide.with(requireActivity()).load(mStackBangumi.getBangumi().getCover())
                .transform(new CenterCrop(),
                        new RoundedCorners(AnimationUtil.dp2px(requireActivity(), 3)))
                .into(cover);

        ji.setText(mStackBangumi.getBangumi().getLatestJi());
        staff.setText(info.getStaff());
        name.setText(mStackBangumi.getBangumiName());
        niandai.setText(info.getNiandai());
        intro.setText(info.getIntro());
        type.setText(info.getType());
        cast.setText(info.getCast());
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isFristLoading) {
            LoadingPageData();
            isFristLoading = false;
        }
        if (mPlayerVideoview.isInPlaybackState()) {
            if (!isuserPuase) {
                mPlayerVideoview.resume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mPlayerVideoview.isPlaying()) {
            mPlayerVideoview.pause();
        }

    }

    @Override
    public void showBangumiJiList(List<TextItemSelectBean> jiList) {
        if (jiList != null && !jiList.isEmpty()) {
            if (mStackBangumi.getPlayedJi() == null) {
                mStackBangumi.setPlayedJi(jiList);
                mStackBangumi.setJiTotal(jiList.size());
            }
            jiList.get(mStackBangumi.getCurrentJi()).setSelect(true);
            mPlayerPresenter.getPlayerUrl(mStackBangumi.getBangumiId(), mStackBangumi.getCurrentJi());
        }
        mContentAdapter.notifijiListChange(jiList);
    }

    @Override
    public void setPlayerUrl(String url) {
        DataSource dataSource = new DataSource(url);
        String title = mStackBangumi.getBangumiName() + " " +
                mContentAdapter.getJiList().get(mStackBangumi.getCurrentJi()).getText();

        dataSource.setTitle(title);
        mPlayerVideoview.setDataSource(dataSource);
        mStackBangumi.setPlayingUrl(url);
        //WiFi情况下才开始播放, 不是Wifi情况下通知相应Cover显示
        boolean playInMobileNet = PreferenceUtil.getBollean(getString(R.string.key_play_no_wifi), false);
        if (NetworkUtils.isWifiConnected(requireContext()) || playInMobileNet) {
            mPlayerVideoview.start();
        } else {
            mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.NOTIFI_ERROR_COVER_SHOW, true, true);
        }

    }

    @Override
    public void showRecommendBangumis(List<Bangumi> recommendBangumis) {
        if (mStackBangumi.getRecommendBangumis() == null) {
            mStackBangumi.setRecommendBangumis(recommendBangumis);
        }
        mContentAdapter.notifiRecommendBangumisChange(recommendBangumis);
    }

    @Override
    public void changeFavoriteButtonState(boolean isFavourite) {
        mStackBangumi.getBangumi().setFavorite(isFavourite);
        mContentAdapter.notifiBangumiChange();
    }

    @Override
    public void showSkipDialog(String url) {
        mStackBangumi.setPlayingUrl(url);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("无法解析")
                .setMessage("是否跳转到解析源")
                .setOnCancelListener(dialog ->  {
                    dialog.dismiss();
                    onBackPressed();
                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .setPositiveButton("确认", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    requireActivity().startActivity(intent);
                    onBackPressed();
                });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downLoadVideo();
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                        .setTitle("提示")
                        .setMessage("如果不授权储存权限, 将无法下载视频喔")
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("去授权", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            requireActivity().startActivity(intent);
                        });
                builder.show();
            } else {
                Toast.makeText(getContext(), "你取消了授权", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        mPlayerVideoview.stopPlayback();
        super.onDestroyView();
    }

    @Override
    public void showResultError(Throwable throwable) {
        int netState = NetworkUtils.getNetworkState(requireContext());
        String text = "解析出错";
        LogUtil.i(throwable.toString());
        mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.NOTIFI_ERROR_COVER_SHOW, true, true);
        if (netState == -1) {
            text = "请联网后重试";
        }
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(mPlayerVideoview, text, Snackbar.LENGTH_LONG);
            mSnackbar.show();
        } else {
            if (!mSnackbar.isShown()) {
                mSnackbar.setText(text).show();
            }
        }
    }

    @Override
    public void showResultEmpty() {

    }

    @Override
    public void onDestroy() {
        if (mServiceConnection != null) {
            requireActivity().unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(SCREEN_STATE_KEY, isFullScreen());
        outState.putParcelable(STACK_BANGUM_KEY, mStackBangumi);
        super.onSaveInstanceState(outState);
    }
}
