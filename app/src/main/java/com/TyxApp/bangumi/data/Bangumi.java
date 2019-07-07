package com.TyxApp.bangumi.data;

import com.google.gson.annotations.SerializedName;

public class Bangumi {
    @SerializedName(value = "id")
    private int vod_id;
    private String soure;
    private String name;
    @SerializedName(value = "pic")
    private String cover;
    private String img;
    private String intro;
    private String hits;
    private String remarks;
    private String total;
    private String serial;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public Bangumi(int vod_id, String soure, String name, String cover) {
        this.vod_id = vod_id;
        this.soure = soure;
        this.name = name;
        this.cover = cover;
    }

    public int getVod_id() {
        return vod_id;
    }

    public void setVod_id(int vod_id) {
        this.vod_id = vod_id;
    }

    public String getSoure() {
        return soure;
    }

    public void setSoure(String soure) {
        this.soure = soure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

}
