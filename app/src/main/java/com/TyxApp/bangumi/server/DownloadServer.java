package com.TyxApp.bangumi.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.VideoDownloadTask;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.data.source.local.VideoDownloadTaskDao;
import com.TyxApp.bangumi.util.HttpRequestUtil;
import com.TyxApp.bangumi.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadServer extends Service {
    private DownloadBinder mBinder;

    private static final int STATE_ERROR = 3;
    private static final int STATE_FINISH = 2;
    private static final int STATE_AWAIT = 0;
    private static final int STATE_PAUSE = 4;
    private static final int STATE_DOWNLOADING = 1;

    private OkHttpClient mClient;
    private BangumiDao mBangumiDao;
    private Call mCall;
    private VideoDownloadTaskDao mTaskDao;
    private Disposable mDisposable;
    private boolean hasTaskDownLoading;
    private VideoDownloadTask mCurrentTask;
    private static final String BASE_DOWNLOAD_PATH_FORMAT =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bangumi/%s/%s";


    @Override
    public void onCreate() {
        super.onCreate();
        mClient = HttpRequestUtil.getClient();
        mDisposable = new CompositeDisposable();
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mTaskDao = AppDatabase.getInstance().getVideoDownloadStackDao();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new DownloadBinder();
        }
        return mBinder;
    }


    class DownloadBinder extends Binder implements Download {

        @Override
        public void addStack(Bangumi bangumi, String videoUrl, String fileName) {
            Single.just(videoUrl)
                    .map(url -> mTaskDao.hasSaveInQueue(url))
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
                        if (task == null) {
                            String dirPath = String.format(BASE_DOWNLOAD_PATH_FORMAT, bangumi.getVideoSoure(), bangumi.getVodId());
                            task = new VideoDownloadTask(dirPath, fileName + ".mp4", bangumi.getVodId(), bangumi.getVideoSoure(), videoUrl);
                            task.id = (int) mTaskDao.insert(task);
                        }
                        return task;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(task -> {
                        if (mCurrentTask == null) {
                            mCurrentTask = task;
                            start();
                        }
                    });
        }

        @Override
        public void start() {
            if (hasTaskDownLoading) {
                return;
            }
            Observable.just(mCurrentTask)
                    .map(task -> setFileLength())
                    .flatMap(task -> Observable.create(new DownloadSubscribe()))
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(() -> updateTaskState(STATE_FINISH))
                    .doOnError(throwable -> updateTaskState(STATE_ERROR))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            videoDownloadTask -> {},
                            throwable -> {
                                Toast.makeText(DownloadServer.this, mCurrentTask.getFileName() + "下载出错", Toast.LENGTH_SHORT).show();
                                nextTask();
                            },
                            () -> nextTask());

        }

        private VideoDownloadTask setFileLength() throws IOException {
            File dirFile = new File(mCurrentTask.getDirPath());
            long total = getFileTotal(mCurrentTask.getUrl());
            long downloadLength = 0;
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            } else {
                File videoFile = new File(mCurrentTask.getPath());
                if (videoFile.exists()) {
                    downloadLength = videoFile.length();
                } else {
                    videoFile.createNewFile();
                }
            }
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

        @Override
        public void pause(String url) {
            if (hasTaskDownLoading && mCall != null) {
                mCall.cancel();
                if (mDisposable != null) {
                    mDisposable.dispose();
                }
                nextTask();
            }
        }

        private void nextTask() {
            hasTaskDownLoading = false;
            Observable.create(new ObservableOnSubscribe<VideoDownloadTask>() {
                @Override
                public void subscribe(ObservableEmitter<VideoDownloadTask> emitter) throws Exception {
                    VideoDownloadTask task = mTaskDao.getAwaitDownloadTasks();
                    if (task == null) {//null为数据库中没有等待的任务
                        emitter.onComplete();
                    } else {
                        emitter.onNext(task);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            task -> {
                                mCurrentTask = task;
                                start();
                            },
                            throwable -> LogUtil.i(throwable.toString()),
                            () -> stopSelf());
        }
    }

    private class DownloadSubscribe implements ObservableOnSubscribe<VideoDownloadTask> {

        @Override
        public void subscribe(ObservableEmitter<VideoDownloadTask> emitter) throws Exception {
            updateTaskState(STATE_DOWNLOADING);
            long downloadLength = mCurrentTask.getDownloadLength();
            long contentLength = mCurrentTask.getTotal();

            emitter.onNext(mCurrentTask);

            Request request = new Request.Builder()
                    .url(mCurrentTask.getUrl())
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .build();

            mCall = mClient.newCall(request);
            Response response = mCall.execute();
            byte[] buffer = new byte[2048];
            int length;
            File videoFile = new File(mCurrentTask.getPath());
            hasTaskDownLoading = true;
            try (InputStream inputStream = response.body().byteStream();
                 FileOutputStream outputStream = new FileOutputStream(videoFile, true)) {

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                    downloadLength += length;
                    mCurrentTask.setDownloadLength(downloadLength);
                    emitter.onNext(mCurrentTask);
                }
                emitter.onComplete();
                hasTaskDownLoading = false;
            } catch (Exception e) {
                emitter.onError(e);
            }
        }
    }

    private int updateTaskState(int state) {
        mCurrentTask.setState(state);
        return mTaskDao.update(mCurrentTask);
    }

}
