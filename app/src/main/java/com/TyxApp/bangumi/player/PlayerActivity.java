package com.TyxApp.bangumi.player;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.base.BaseMvpActivity;
import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.data.source.remote.Dilidili;
import com.TyxApp.bangumi.data.source.remote.IBangumiParser;
import com.TyxApp.bangumi.data.source.remote.Nico;
import com.TyxApp.bangumi.data.source.remote.Qimiqimi;
import com.TyxApp.bangumi.data.source.remote.Sakura;
import com.TyxApp.bangumi.data.source.remote.Silisili;
import com.TyxApp.bangumi.data.source.remote.ZzzFun;
import com.TyxApp.bangumi.player.adapter.PlayerAdapter;
import com.TyxApp.bangumi.player.bottomsheet.DanmakuSetingBottomSheet;
import com.TyxApp.bangumi.player.bottomsheet.DetailBottomSheet;
import com.TyxApp.bangumi.player.bottomsheet.MainBottomSheet;
import com.TyxApp.bangumi.player.bottomsheet.VideoSpeedBottomSheet;
import com.TyxApp.bangumi.player.cover.DanmakuCover;
import com.TyxApp.bangumi.player.cover.ErrorCover;
import com.TyxApp.bangumi.player.cover.GestureCover;
import com.TyxApp.bangumi.player.cover.LoadingCover;
import com.TyxApp.bangumi.player.cover.PlayerControlCover;
import com.TyxApp.bangumi.server.DownloadBinder;
import com.TyxApp.bangumi.server.DownloadServer;
import com.TyxApp.bangumi.util.AnimationUtil;
import com.TyxApp.bangumi.util.PreferenceUtil;
import com.TyxApp.bangumi.view.ParallaxBaseVideoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.utils.NetworkUtils;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import java.util.List;

import butterknife.BindView;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public class PlayerActivity extends BaseMvpActivity implements PlayContract.View {
    @BindView(R.id.transparentToolbar)
    Toolbar mToolbar;
    @BindView(R.id.videoView)
    ParallaxBaseVideoView mVideoview;
    @BindView(R.id.infoRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.gradualToolbar)
    View gradualToolbar;

    public static final String INTENT_KEY = "bangumi_key";
    private static final String SAVE_DATA_KEY = "s_d_k";

    private int topPoint;//记录RecyclerView顶点位置

    private boolean isUserPause;
    private ServiceConnection mServiceConnection;
    private DownloadBinder mDownloadBinder;
    private ReceiverGroup mReceiverGroup;
    private int videoViewformerHeighe;
    private boolean isfristLoading = true;//是否第一次加载
    private int mCurrentJi;//当前播放的集数
    private SparseArray<VideoUrl> mPlayerurls = new SparseArray<>();//播放过的视频url
    private int jiCount;//解析到的集数
    private boolean hasDanmaku;//是否有弹幕


    private ViewGroup mInfoViewGroup;
    private PlayerAdapter mAdapter;
    private Bangumi mBangumi;
    private PlayContract.Presenter mPresenter;
    private SensorEventListener mSensorEventListener;
    private OnVideoViewEventHandler mEventHandler = new OnVideoViewEventHandler() {
        @Override
        public void requestPause(BaseVideoView videoView, Bundle bundle) {
            if (mInfoViewGroup.getTop() != 0) {
                topPoint = mInfoViewGroup.getTop();
            }
            if (videoView.isInPlaybackState()) {
                videoView.pause();
                isUserPause = true;
                if (!isFullScreen()) {
                    gradualToolbar.setVisibility(View.VISIBLE);
                    showStateBar();
                }
            }
        }

        @Override
        public void requestResume(BaseVideoView videoView, Bundle bundle) {
            if (mInfoViewGroup.getTop() != 0) {
                gradualToolbar.getBackground().setAlpha(0);
                mVideoview.setOfferSet(0);
                mRecyclerView.scrollToPosition(0);
            }
            if (videoView.isInPlaybackState()) {
                videoView.resume();
                isUserPause = false;
                if (!isFullScreen()) {
                    gradualToolbar.setVisibility(View.GONE);
                    hindStateBar();
                }
            } else {
                videoView.start();
            }
        }

        @Override
        public void requestRetry(BaseVideoView videoView, Bundle bundle) {
            super.requestRetry(videoView, bundle);
            if (mAdapter.getJiList() == null) {
                mPresenter.getBangumiInfo(mBangumi.getVideoId());
            }

        }

        @Override
        public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            coverEventHandle(assist, eventCode, bundle);
        }
    };

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (!recyclerView.canScrollVertically(-1)) {
                if (topPoint != 0) {
                    topPoint = 0;
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (mVideoview.isInPlaybackState() && mVideoview.getState() == IPlayer.STATE_PAUSED) {
                int scrollY = mInfoViewGroup.getTop();
                if (topPoint != 0) {
                    scrollY = scrollY - topPoint;
                }
                if (topPoint != 0 && scrollY > 0) {
                    scrollY = 0;
                }
                mVideoview.setOfferSet(scrollY);
                float alpha = (float) scrollY / (float) mVideoview.getMinOffset();
                alpha = Math.abs(alpha * 255);
                if (alpha > 255) {
                    alpha = 255;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                gradualToolbar.getBackground().setAlpha((int) alpha);
            }
        }
    };
    private boolean isActive;

    @Override
    public BasePresenter getPresenter() {
        Intent intent = getIntent();
        intent.setExtrasClassLoader(Bangumi.class.getClassLoader());
        mBangumi = intent.getParcelableExtra(INTENT_KEY);
        IBangumiParser parser = null;
        switch (mBangumi.getVideoSoure()) {
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

            case BangumiPresistenceContract.BangumiSource.SILISILI:
                parser = Silisili.newInstance();
                break;

            case BangumiPresistenceContract.BangumiSource.QIMIQIMI:
                parser = Qimiqimi.newInstance();
                break;
        }
        mPresenter = new PlayerPresenter(parser, this);
        return mPresenter;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.overlay_drak));
        if (savedInstanceState != null) {
            mCurrentJi = savedInstanceState.getInt(SAVE_DATA_KEY, 0);
        }
        initToolbar();
        initRecyclerView();
        initVideoView();
        hindStateBar();
    }

    private void initVideoView() {
        mReceiverGroup = new ReceiverGroup();
        mReceiverGroup.addReceiver(DanmakuCover.class.getName(), new DanmakuCover(this));
        mReceiverGroup.addReceiver(PlayerControlCover.class.getName(), new PlayerControlCover(this));
        mReceiverGroup.addReceiver(LoadingCover.class.getName(), new LoadingCover(this));
        mReceiverGroup.addReceiver(GestureCover.class.getName(), new GestureCover(this));
        mReceiverGroup.addReceiver(ErrorCover.class.getName(), new ErrorCover(this));
        mVideoview.setReceiverGroup(mReceiverGroup);
        mVideoview.setEventHandler(mEventHandler);
        mVideoview.post(() -> {
            videoViewformerHeighe = mVideoview.getHeight();
            if (isFullScreen()) {
                Configuration configuration = new Configuration();
                configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
                onConfigurationChanged(configuration);
            }
        });
        mVideoview.setOnPlayerEventListener((eventCode, bundle) -> {
            if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_START) {
                if (!isActive) {
                    mVideoview.pause();
                }
            }
        });
    }

    private void coverEventHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
        switch (eventCode) {
            case VideoPlayerEvent.Code.CODE_FULL_SCREEN://屏幕旋转
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;

            case VideoPlayerEvent.Code.CODE_BACK://返回图标按下
                if (isFullScreen()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    finish();
                }
                break;

            case VideoPlayerEvent.Code.CODE_CONTROL_VIEW_SHOW://进度控制显示和消失
                if (!isFullScreen()) {
                    if (bundle.getBoolean(VideoPlayerEvent.Key.CONTROL_VIEW_SHOW)) {
                        gradualToolbar.setVisibility(View.VISIBLE);
                    } else {
                        if (mVideoview.getState() != IPlayer.STATE_PAUSED) {
                            gradualToolbar.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case VideoPlayerEvent.Code.CODE_NEXT://下一集
                if (mCurrentJi == jiCount - 1) {
                    Toast.makeText(this, "已经是最后一集啦", Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentJi++;
                    if (mPlayerurls.get(mCurrentJi) == null) {
                        mPresenter.getPlayerUrl(mBangumi.getVideoId(), mCurrentJi);
                    } else {
                        setPlayerUrl(mPlayerurls.get(mCurrentJi));
                    }
                    mAdapter.setJiSelect(mCurrentJi);
                }
                break;

            case VideoPlayerEvent.Code.CODE_ERROR_COVER_SHOW:
                boolean isShow = bundle.getBoolean(EventKey.BOOL_DATA);
                if (isShow) {
                    assist.getSuperContainer().setGestureEnable(false);
                } else {
                    assist.getSuperContainer().setGestureEnable(true);
                }
                break;

            case VideoPlayerEvent.Code.CODE_PLAYER_MORE_CLICK:
                showMainBottomSheet();
                break;

            case VideoPlayerEvent.Code.CODE_DANMAKU_PREPARED:
                if (!mVideoview.isInPlaybackState() && !mReceiverGroup.getGroupValue().getBoolean(VideoPlayerEvent.Key.ERROR_COVER_SHOW)) {
                    mVideoview.start();
                }
                break;
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInfoViewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.item_player_bangumi_info, mRecyclerView, false);
        View infoRootView = mInfoViewGroup.findViewById(R.id.rootView);
        infoRootView.setOnClickListener(v -> DetailBottomSheet.newInstance(mBangumi).show(getSupportFragmentManager(), DetailBottomSheet.class.getName()));

        mAdapter = new PlayerAdapter(mInfoViewGroup, this);
        mAdapter.setOnItemClckListemer(new PlayerAdapter.OnItemClckListemer() {
            @Override
            public void onJiClick(int position) {
                if (mPlayerurls.get(position) == null) {

                    mPresenter.getPlayerUrl(mBangumi.getVideoId(), position);
                } else {
                    setPlayerUrl(mPlayerurls.get(position));
                }
                mCurrentJi = position;
            }

            @Override
            public void onbangumiItemSelect() {
                if (mVideoview.isInPlaybackState()) {
                    mVideoview.stop();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mInfoViewGroup.findViewById(R.id.like_button).setOnClickListener(v -> {
            if (v.isSelected()) {
                ((Button) v).setText("追番");
                mBangumi.setFavorite(false);
            } else {
                ((Button) v).setText("已追番");
                mBangumi.setFavorite(true);
            }
            mPresenter.setFavorite(mBangumi);
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> finish());

        //获取状态栏高度
        int stateBarheight = 0;
        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            stateBarheight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        if (stateBarheight == 0) {
            stateBarheight = 96;
        }
        findViewById(R.id.stateBar).getLayoutParams().height = stateBarheight;

        gradualToolbar.getBackground().setAlpha(0);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLandscape;
        int videoViewHeight;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (gradualToolbar.getVisibility() == View.VISIBLE) {
                gradualToolbar.setVisibility(View.GONE);
            }
            isLandscape = true;
            videoViewHeight = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            if (mVideoview.getState() == IPlayer.STATE_PAUSED) {
                gradualToolbar.setVisibility(View.VISIBLE);
            }
            isLandscape = false;
            videoViewHeight = videoViewformerHeighe;
        }
        ViewGroup.LayoutParams layoutParams = mVideoview.getLayoutParams();
        layoutParams.height = videoViewHeight;
        mVideoview.requestLayout();
        mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.IS_FULLSCREEN_KEY, isLandscape, true);
        hindStateBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.player_more) {
            showMainBottomSheet();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMainBottomSheet() {
        String[] itemTexts = getResources().getStringArray(R.array.main_bottomsheet_item);
        MainBottomSheet mainBottomSheet = MainBottomSheet.newInstance(itemTexts);
        mainBottomSheet.setOnItemClickListener(pos -> {
            mainBottomSheet.dismiss();
            switch (pos) {
                case 0://重播
                    mVideoview.rePlay(0);
                    break;
                case 1://调速
                    showVideoSpeedBottomSheet();
                    break;
                case 2://下载
                    String url = mPlayerurls.get(mCurrentJi) == null ? "" : mPlayerurls.get(mCurrentJi).getUrl();
                    if (TextUtils.isEmpty(url) || url.contains(".html")
                            || url.contains(".m3u8")) {

                        Toast.makeText(this, "暂时无法下载此视频", Toast.LENGTH_SHORT).show();
                    } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        downLoadVideo();
                    }
                    break;
                case 3://弹幕设置
                    if (hasDanmaku) {
                        boolean isShow = mReceiverGroup.getGroupValue().getBoolean(VideoPlayerEvent.Key.DANMAKU_VISIBLE);
                        DanmakuSetingBottomSheet bottomSheet = DanmakuSetingBottomSheet.newInstance(isShow);
                        bottomSheet.setOnCheckedChangeListener(isCheck -> mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.DANMAKU_VISIBLE, isCheck, true));
                        bottomSheet.show(getSupportFragmentManager(), DanmakuSetingBottomSheet.class.getName());
                    } else {
                        Toast.makeText(this, R.string.toast_no_danmaku, Toast.LENGTH_SHORT).show();
                    }
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
                    mVideoview.setSpeed(0.5f);
                    break;
                case 1:
                    mVideoview.setSpeed(0.7f);
                    break;
                case 3:
                    mVideoview.setSpeed(1.5f);
                    break;
                case 4:
                    mVideoview.setSpeed(2.0f);
                    break;
                default:
                    mVideoview.setSpeed(1.0f);
            }
        });
        speedBottomSheet.show(getSupportFragmentManager(), VideoSpeedBottomSheet.class.getName());
    }

    private void showStateBar() {
        final int UI_STATE = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(UI_STATE);
    }

    private void hindStateBar() {
        int uiFlage = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (isFullScreen()) {
            uiFlage |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(uiFlage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        registerSensor();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mVideoview.isInPlaybackState()) {
            if (!isUserPause) {
                mVideoview.resume();
            }
        } else if (mVideoview.getState() == IPlayer.STATE_STOPPED) {
            mVideoview.rePlay(mVideoview.getCurrentPosition());
        }
        if (isfristLoading) {
            isfristLoading = false;
            mBangumi.setHistoryTime(System.currentTimeMillis());
            mPresenter.setTime(mBangumi);
            mPresenter.checkFavorite(mBangumi.getVideoId(), mBangumi.getVideoSoure());
            mPresenter.getBangumiInfo(mBangumi.getVideoId());
        }
    }

    private void registerSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravitySensor != null) {
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (isFullScreen()) {
                        if (event.values[0] >= 8.5f) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else if (event.values[0] <= -8.5f) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            sensorManager.registerListener(mSensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
        if (mVideoview.isInPlaybackState()) {
            mVideoview.pause();
        }
        unRegisterSensor();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void unRegisterSensor() {
        if (mSensorEventListener != null) {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mSensorEventListener);
        }
    }

    /**
     * 权限回调处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downLoadVideo();
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("如果不授权储存权限, 将无法下载视频喔")
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("去授权", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
                builder.show();
            } else {
                Toast.makeText(this, "你取消了授权", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_player;
    }

    private boolean isFullScreen() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 开启服务下载视频
     */
    private void downLoadVideo() {
        if (jiCount == 0) {
            Toast.makeText(this, "没有视频可以下载", Toast.LENGTH_SHORT).show();
            return;
        }
        mBangumi.setDownLoad(true);
        mPresenter.setDownload(mBangumi);
        if (mDownloadBinder != null) {
            String fileName = mAdapter.getJiList().get(mCurrentJi).getText();
            mDownloadBinder.addTask(mBangumi.getVideoId(), mBangumi.getVideoSoure(), mPlayerurls.get(mCurrentJi).getUrl(), fileName);
        } else {
            Intent intent = new Intent(this, DownloadServer.class);
            startService(intent);
            if (mServiceConnection == null) {
                mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        mDownloadBinder = (DownloadBinder) service;
                        String fileName = mAdapter.getJiList().get(mCurrentJi).getText();
                        mDownloadBinder.addTask(mBangumi.getVideoId(), mBangumi.getVideoSoure(), mPlayerurls.get(mCurrentJi).getUrl(), fileName);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                };
            }
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 回调显示番剧信息
     */
    @Override
    public void showBangumiInfo(BangumiInfo info) {
        if (info != null) {
            mBangumi.setBangumiInfo(info);
        }
        setInfoView();
        mPresenter.getBangumiJiList(mBangumi.getVideoId());
    }

    private void setInfoView() {
        ImageView cover = mInfoViewGroup.findViewById(R.id.cover);
        TextView name = mInfoViewGroup.findViewById(R.id.name);
        TextView intro = mInfoViewGroup.findViewById(R.id.intro);
        TextView ji = mInfoViewGroup.findViewById(R.id.ji);
        name.setText(mBangumi.getName());
        ji.setText(mBangumi.getLatestJi());
        intro.setText(mBangumi.getIntro());
        Glide.with(this).load(mBangumi.getCover())
                .error(R.drawable.image_error)
                .transform(new CenterCrop(), new RoundedCorners(AnimationUtil.dp2px(this, 3)))
                .into(cover);
    }

    /**
     * 显示集
     */
    @Override
    public void showBangumiJiList(List<TextItemSelectBean> jiList) {
        if (jiList != null && !jiList.isEmpty()) {
            jiCount = jiList.size();
            mPresenter.getRecommendBangumis(mBangumi.getVideoId());
            jiList.get(mCurrentJi).setSelect(true);
            mPresenter.getPlayerUrl(mBangumi.getVideoId(), mCurrentJi);
        }
        //显示追番按钮
        View view = mInfoViewGroup.findViewById(R.id.like_button);
        view.setVisibility(View.VISIBLE);
        AnimationUtil.popAnima(view);

        mAdapter.setJiList(jiList);
    }

    /**
     * 获得视频地址开始播放
     */
    @Override
    public void setPlayerUrl(VideoUrl videoUrl) {
        if (videoUrl == null) {
            Toast.makeText(this, "视频解析失败", Toast.LENGTH_SHORT).show();
        } else if (videoUrl.isHtml()) {
            showSkipDialo(videoUrl.getUrl());
        } else {
            DataSource dataSource = new DataSource(videoUrl.getUrl());
            dataSource.setTitle(String.format("%S %S", mBangumi.getName(), mAdapter.getJiList().get(mCurrentJi).getText()));
            mVideoview.setDataSource(dataSource);
            mPlayerurls.append(mCurrentJi, videoUrl);
            //WiFi情况下才开始播放, 不是Wifi情况下通知相应Cover显示
            boolean playInMobileNet = PreferenceUtil.getBollean(getString(R.string.key_play_no_wifi), false);
            mPresenter.getDanmaku(mBangumi.getVideoId(), mCurrentJi);
            if (!NetworkUtils.isWifiConnected(this) || !playInMobileNet) {
                mReceiverGroup.getGroupValue().putBoolean(VideoPlayerEvent.Key.NOTIFI_ERROR_COVER_SHOW, true, true);
            }
        }
    }

    /**
     * 设置弹幕
     */
    @Override
    public void setDanmaku(BaseDanmakuParser danmakuParser) {
        //有弹幕就等弹幕准备完成通知视频一起开始
        if (danmakuParser != null) {
            hasDanmaku = true;
            DanmakuCover danmakuCover = mReceiverGroup.getReceiver(DanmakuCover.class.getName());
            danmakuCover.setParser(danmakuParser);
        } else {
            hasDanmaku = false;
            if (!mVideoview.isInPlaybackState() && !mReceiverGroup.getGroupValue().getBoolean(VideoPlayerEvent.Key.ERROR_COVER_SHOW)) {
                mVideoview.start();
            }
        }
    }

    private void showSkipDialo(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("无法解析本集")
                .setMessage("是否跳转到解析源")
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确认", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    onBackPressed();
                });
        builder.show();
    }

    @Override
    public void showRecommendBangumis(List<Bangumi> recommendBangumis) {
        mAdapter.setRecommenBangumis(recommendBangumis);
    }

    @Override
    public void showFavoriteButton(boolean isFavourite) {
        Button button = mInfoViewGroup.findViewById(R.id.like_button);
        if (button.isSelected() != isFavourite) {
            AnimationUtil.popAnima(button);
        }
        button.setSelected(isFavourite);
        if (isFavourite) {
            button.setText("已追番");
        }
    }

    public static void startPlayerActivity(Context context, Bangumi bangumi) {
        if (!NetworkUtils.isNetConnected(context)) {
            Toast.makeText(context, "无网情况下个别手机进入此页面会闪退, 所以请联网重试", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(INTENT_KEY, bangumi);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mReceiverGroup.clearReceivers();
        mVideoview.stopPlayback();
        mReceiverGroup = null;
        mVideoview = null;
        mRecyclerView.setAdapter(null);
        mRecyclerView.setItemAnimator(null);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss();
        }
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(SAVE_DATA_KEY, mCurrentJi);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
