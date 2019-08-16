package com.TyxApp.bangumi.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.TyxApp.bangumi.R;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.data.source.local.VideoDownloadTaskDao;
import com.TyxApp.bangumi.downloaddetails.DownloadDetailsActivity;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadServer extends Service {
    private DownloadBinder mBinder;

    public static final int STATE_ERROR = 3;
    public static final int STATE_FINISH = 2;
    public static final int STATE_AWAIT = 0;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_DOWNLOADING = 1;

    public static final int NOTIFICATION_ID = 8;
    public static final String INTENT_KEY = "I_K";
    private static final String BASE_DOWNLOAD_PATH_FORMAT =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bangumi/%s/%s";

    private OkHttpClient mClient;
    private BangumiDao mBangumiDao;
    private VideoDownloadTaskDao mTaskDao;
    private boolean isTaskDownLoading;
    private VideoDownloadTask mCurrentTask;
    private VideoDownloadTask interruptTask;
    private Notification.Builder mNotificationBuilder;
    private onProgressUpdateLintener mProgressUpdateLintener;
    private boolean isPause;
    private Disposable mDisposable;


    @Override
    public void onCreate() {
        super.onCreate();
        mClient = HttpRequestUtil.getClient();
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mTaskDao = AppDatabase.getInstance().getVideoDownloadStackDao();

        NotificationChannel channel = new NotificationChannel("download", "下载进度", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean isAutoRecoverDownload = intent.getBooleanExtra(INTENT_KEY, false);
            if (isAutoRecoverDownload) {
                mTaskDao.getUnfinishedTasks()
                        .toFlowable()
                        .flatMap(tasks -> Flowable.fromIterable(tasks))
                        .subscribeOn(Schedulers.io())
                        .doOnNext(task -> {
                            task.setState(STATE_AWAIT);
                            mTaskDao.update(task);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> nextTask())
                        .subscribe();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new DownloadBinder();
        }
        return mBinder;
    }

    public void addTask(String bangumiId, String sourch, String videoUrl, String fileName) {
        Single.just(videoUrl)
                .map(url -> mTaskDao.getId(url))
                .map(id -> {
                    VideoDownloadTask task = null;
                    if (id != 0) {
                        task = mTaskDao.getTaskState(id);
                        if (new File(task.getPath()).exists()) {
                            if (task.getState() != STATE_FINISH) {
                                task.setState(STATE_AWAIT);
                                mTaskDao.update(task);
                            }
                        } else {
                            mTaskDao.delete(task);
                            task = null;//文件被手动删除要重新创建加入任务队列
                        }
                    }
                    //创建新任务
                    if (task == null) {
                        String dirPath = String.format(BASE_DOWNLOAD_PATH_FORMAT, sourch, bangumiId);
                        task = new VideoDownloadTask(dirPath, fileName, bangumiId, sourch, videoUrl);
                        File dirFile = new File(dirPath);
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }
                        File videoFile = new File(task.getPath());
                        if (!videoFile.exists()) {
                            videoFile.createNewFile();
                        }
                        task.setTotal(getFileTotal(videoUrl));
                        task.id = (int) mTaskDao.insert(task);
                    }
                    return task;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(task -> {
                    if (!isTaskDownLoading && mCurrentTask == null) {
                        mCurrentTask = task;
                        start();
                    }
                });
    }

    public void start() {
        if (isTaskDownLoading) {
            return;
        }
        isTaskDownLoading = true;
        mDisposable = Observable.just(mCurrentTask)
                .map(task -> setFileLength())//获取文件长度
                .flatMap(task -> Observable.create(new DownloadSubscribe()))//开始下载
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> updateTaskState(STATE_FINISH))
                .doOnError(throwable -> updateTaskState(STATE_ERROR))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> nextTask())
                .subscribe(
                        videoDownloadTask -> {
                            if (mProgressUpdateLintener != null) {
                                mProgressUpdateLintener.onProgressUpdate(videoDownloadTask.getDownloadLength(), videoDownloadTask.getTotal());
                            }
                        },
                        throwable -> Toast.makeText(this, "下载出错", Toast.LENGTH_SHORT).show());

    }

    private VideoDownloadTask setFileLength() throws IOException {
        File dirFile = new File(mCurrentTask.getDirPath());
        long total = mCurrentTask.getTotal();
        if (total == 0) {
            total = getFileTotal(mCurrentTask.getUrl());
        }
        long downloadLength = 0;
        File videoFile = new File(mCurrentTask.getPath());
        downloadLength = videoFile.length();

        mCurrentTask.setDownloadLength(downloadLength);
        mCurrentTask.setTotal(total);

        return mCurrentTask;
    }

    private long getFileTotal(String videoUrl) throws IOException {
        Request request = new Request.Builder()
                .url(videoUrl)
                .build();
        Response response = mClient.newCall(request).execute();
        long toatl = 0;
        if (response.body() != null) {
            toatl = response.body().contentLength();
        }
        return toatl;
    }

    public void pause() {
        if (isTaskDownLoading) {
            isPause = true;
        }
    }

    private void nextTask() {
        isTaskDownLoading = false;
        if (interruptTask != null) {
            mCurrentTask = interruptTask;
            interruptTask = null;
            start();
        } else {
            Observable.create(emitter -> {
                mCurrentTask = mTaskDao.getDownloadTask(STATE_AWAIT);
                if (mCurrentTask == null) {//null为数据库中没有等待的任务
                    emitter.onComplete();
                } else {
                    emitter.onNext(mCurrentTask);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> {
                        stopForeground(NOTIFICATION_ID);
                        stopSelf();
                    })
                    .subscribe(task -> start());
        }
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<VideoDownloadTask> {

        @Override
        public void subscribe(ObservableEmitter<VideoDownloadTask> emitter) throws Exception {
            updateTaskState(STATE_DOWNLOADING);
            updateNotification();
            long downloadLength = mCurrentTask.getDownloadLength();
            long contentLength = mCurrentTask.getTotal();

            Request request = new Request.Builder()
                    .url(mCurrentTask.getUrl())
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .build();

            Call call = mClient.newCall(request);
            Response response = call.execute();
            byte[] buffer = new byte[2048];
            int length;
            File videoFile = new File(mCurrentTask.getPath());
            try (InputStream inputStream = response.body().byteStream();
                 FileOutputStream outputStream = new FileOutputStream(videoFile, true)) {

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                    downloadLength += length;

                    mNotificationBuilder.setProgress((int) contentLength, (int) downloadLength, false);
                    startForeground(DownloadServer.NOTIFICATION_ID, mNotificationBuilder.build());

                    mCurrentTask.setDownloadLength(downloadLength);
                    if (isPause) {
                        call.cancel();
                        response.close();
                        updateTaskState(STATE_PAUSE);
                        mDisposable.dispose();
                        isPause = false;
                        return;
                    }
                    emitter.onNext(mCurrentTask);
                }
                emitter.onComplete();
                isTaskDownLoading = false;
            } catch (Exception e) {
                emitter.onError(e);
            }
        }
    }

    private void updateNotification() {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(getApplicationContext(), "download")
                    .setSmallIcon(R.drawable.ic_bottom_sheet_download)
                    .setWhen(System.currentTimeMillis());
        }
        Intent intent = new Intent(getApplicationContext(), DownloadDetailsActivity.class);
        intent.putExtra(DownloadDetailsActivity.SOURCH_KEY, mCurrentTask.getBangumiSourch());
        intent.putExtra(DownloadDetailsActivity.ID_KEY, mCurrentTask.getBangumiId());
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(pendingIntent);
        mNotificationBuilder.setContentTitle(mCurrentTask.getFileName());
    }

    private int updateTaskState(int state) {
        mCurrentTask.setState(state);
        return mTaskDao.update(mCurrentTask);
    }


    @Override
    public void unbindService(ServiceConnection conn) {
        mProgressUpdateLintener = null;
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        if (isTaskDownLoading) {
            Single.just(mCurrentTask)
                    .map(task -> updateTaskState(STATE_PAUSE))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> super.onDestroy())
                    .subscribe();
        } else {
            super.onDestroy();
        }
    }

    class DownloadBinder extends Binder implements com.TyxApp.bangumi.server.DownloadBinder {

        @Override
        public void addTask(String bangumiId, String sourch, String videoUrl, String fileName) {
            DownloadServer.this.addTask(bangumiId, sourch, videoUrl, fileName);
        }

        @Override
        public void addTask(VideoDownloadTask task) {
            if (!isTaskDownLoading && mCurrentTask == null) {
                mCurrentTask = task;
                start();
            } else {
                Single.just(task)
                        .map(t -> {
                            t.setState(STATE_AWAIT);
                            return mTaskDao.update(t);
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe();
            }
        }

        @Override
        public void start() {
            DownloadServer.this.start();
        }

        @Override
        public void pause() {
            DownloadServer.this.pause();
        }

        @Override
        public void setProgressUpdateLintener(onProgressUpdateLintener progressUpdateLintener) {
            mProgressUpdateLintener = progressUpdateLintener;
        }

        @Override
        public void setInterruptTask(VideoDownloadTask task) {
            interruptTask = task;
            pause();//暂停现在任务然后执行中断任务
        }
    }

    public static interface onProgressUpdateLintener {
        void onProgressUpdate(long progress, long total);
    }

}
