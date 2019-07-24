package com.TyxApp.bangumi.data.bean;

public class VideoDownloadInfo {
    private String path;
    private String fileName;
    private int bangumiId;
    private String bangumiSourch;
    private int state;
    private long total;
    private long downloadLength;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public VideoDownloadInfo(String path, String fileName, int bangumiId, String bangumiSourch) {
        this.path = path;
        this.fileName = fileName;
        this.bangumiId = bangumiId;
        this.bangumiSourch = bangumiSourch;
    }

    public VideoDownloadInfo() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(int bangumiId) {
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
}
