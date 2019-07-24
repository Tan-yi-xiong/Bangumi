package com.TyxApp.bangumi.server;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.VideoDownloadInfo;
import com.TyxApp.bangumi.data.source.local.AppDatabase;
import com.TyxApp.bangumi.data.source.local.BangumiDao;
import com.TyxApp.bangumi.data.source.local.BangumiPresistenceContract;
import com.TyxApp.bangumi.util.HttpRequestUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadServer extends Service {
    private DownloadBinder mBinder;
    private List<VideoDownloadInfo> mDownloadInfos;
    private Map<String, Call> mCallMap;
    private static final int STATE_ERROR = 3;
    private static final int STATE_FINISH = 2;
    private static final int STATE_AWAIT = 0;
    private static final int STATE_DOWNLOADING = 1;
    private OkHttpClient mClient;
    private BangumiDao mBangumiDao;
    private CompositeDisposable mDisposable;
    private static final String BASE_DOWNLOAD_PATH_FORMAT =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bangumi/%s/%s/%s.mp4";


    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadInfos = new ArrayList<>();
        mClient = HttpRequestUtil.getClient();
        mDisposable = new CompositeDisposable();
        mBangumiDao = AppDatabase.getInstance().getBangumiDao();
        mCallMap = new HashMap<>();
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
            String filePath = String.format(BASE_DOWNLOAD_PATH_FORMAT, bangumi.getVideoSoure(), bangumi.getVodId(), fileName);
            for (VideoDownloadInfo downloadInfo : mDownloadInfos) {
                if (downloadInfo.getUrl().equals(videoUrl)) {
                    return;
                }
            }
            VideoDownloadInfo downloadInfo = new VideoDownloadInfo(filePath, fileName, bangumi.getVodId(), bangumi.getVideoSoure());
            downloadInfo.setState(STATE_AWAIT);
            downloadInfo.setUrl(videoUrl);
            mDownloadInfos.add(downloadInfo);
            start();
        }

        @Override
        public void start() {
            int takeCount = 3 - mCallMap.size();
            if (takeCount <= 0) {
                return;
            }
            mDisposable.add(Flowable.fromIterable(mDownloadInfos)
                    .map(this::getFileLengthAndState)
                    .filter(downloadInfo -> {
                        int state = downloadInfo.getState();
                        return state != STATE_FINISH && state != STATE_DOWNLOADING;
                    })
                    .take(takeCount)
                    .flatMap(downloadInfo -> Flowable.create(new DownloadSubscribe(downloadInfo), BackpressureStrategy.BUFFER))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            downloadInfo -> {
                            },
                            throwable -> start(),
                            this::start));

        }

        private VideoDownloadInfo getFileLengthAndState(VideoDownloadInfo downloadInfo) throws IOException {
            File videoFile = new File(downloadInfo.getPath());
            long total = getFileTotal(downloadInfo.getUrl());
            long downloadLength = 0;

            if (videoFile.exists()) {
                downloadLength = videoFile.length();
                if (total == downloadLength) {
                    downloadInfo.setState(STATE_FINISH);
                }
            }

            downloadInfo.setTotal(total);
            downloadInfo.setDownloadLength(downloadLength);
            return downloadInfo;
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
            mCallMap.remove(url);
            start();
        }
    }

    private class DownloadSubscribe implements FlowableOnSubscribe<VideoDownloadInfo> {
        private VideoDownloadInfo mVideoDownloadInfo;

        public DownloadSubscribe(VideoDownloadInfo videoDownloadInfo) {
            mVideoDownloadInfo = videoDownloadInfo;
        }

        @Override
        public void subscribe(FlowableEmitter<VideoDownloadInfo> emitter) throws Exception {
            mVideoDownloadInfo.setState(STATE_DOWNLOADING);
            long downloadLength = mVideoDownloadInfo.getDownloadLength();
            long contentLength = mVideoDownloadInfo.getTotal();

            emitter.onNext(mVideoDownloadInfo);

            Request request = new Request.Builder()
                    .url(mVideoDownloadInfo.getUrl())
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .build();

            //创建文件夹
            String absolutePath = mVideoDownloadInfo.getPath();
            String dirPath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(mVideoDownloadInfo.getPath());
            if (!file.exists()) {
                file.createNewFile();
            }

            Call call = mClient.newCall(request);
            mCallMap.put(mVideoDownloadInfo.getUrl(), call);
            Response response = call.execute();
            byte[] buffer = new byte[2048];
            int length;
            try (InputStream inputStream = response.body().byteStream();
                 FileOutputStream outputStream = new FileOutputStream(file, true)) {

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                    downloadLength += length;
                    mVideoDownloadInfo.setDownloadLength(downloadLength);
                    emitter.onNext(mVideoDownloadInfo);
                }
                mCallMap.remove(mVideoDownloadInfo.getUrl());
                mDownloadInfos.remove(mVideoDownloadInfo);
                mVideoDownloadInfo.setState(STATE_FINISH);
                emitter.onComplete();
                //下载完将信息存进数据库
                saveToDB(mVideoDownloadInfo);
            } catch (Exception e) {
                mCallMap.remove(mVideoDownloadInfo.getUrl());
                mVideoDownloadInfo.setState(STATE_ERROR);
                saveToDB(mVideoDownloadInfo);
                emitter.onError(e);
            }
        }
    }

    private void saveToDB(VideoDownloadInfo videoDownloadInfo) {

    }

}
