package com.TyxApp.bangumi.data.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "VIDEODOWNLOADTASK",
        indices = @Index(value = {"url"}, unique = true))
public class VideoDownloadTask {
    @PrimaryKey(autoGenerate = true)
    public int id;

    private String dirPath;//视频文件夹路径

    @ColumnInfo(name = "file_name")
    private String fileName;//文件名

    //下面两项用于查找
    @ColumnInfo(name = "bangumi_id")
    private String bangumiId;
    @ColumnInfo(name = "bangumi_sourch")
    private String bangumiSourch;

    private int state;
    private long total;
    @ColumnInfo(name = "download_length")
    private long downloadLength;
    private String url;//视频地址


    public VideoDownloadTask(String path, String fileName, String bangumiId, String bangumiSourch, String url) {
        this.dirPath = path;
        this.fileName = fileName;
        this.bangumiId = bangumiId;
        this.bangumiSourch = bangumiSourch;
        this.url = url;
    }

    public VideoDownloadTask() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(String bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getBangumiSourch() {
        return bangumiSourch;
    }

    public void setBangumiSourch(String bangumiSourch) {
        this.bangumiSourch = bangumiSourch;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getDownloadLength() {
        return downloadLength;
    }

    public void setDownloadLength(long downloadLength) {
        this.downloadLength = downloadLength;
    }

    public String getPath() {
        return dirPath + "/" + fileName;
    }
}
