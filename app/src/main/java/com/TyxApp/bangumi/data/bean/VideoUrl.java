package com.TyxApp.bangumi.data.bean;

public class VideoUrl {
    boolean isHtml;
    String url;

    public VideoUrl() {
    }

    public VideoUrl(boolean isHtml, String url) {
        this.isHtml = isHtml;
        this.url = url;
    }

    public VideoUrl(String url) {
        this.url = url;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
