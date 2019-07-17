package com.TyxApp.bangumi.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Bangumi implements Parcelable {
    @SerializedName(value = "id", alternate = {"vod_id"})
    private int vod_id;

    private String soure;//来源

    @SerializedName(value = "name", alternate = {"vod_name"})
    private String name;

    @SerializedName(value = "pic", alternate = {"vod_pic"})
    private String cover;

    private String img;

    private String intro;//简介

    @SerializedName(value = "hits", alternate = {"vod_hits"})
    private String hits;//热度

    @SerializedName(value = "remarks", alternate = {"vod_remarks"})
    private String remarks;

    public int getJitotal() {
        return Jitotal;
    }

    public void setJitotal(int jitotal) {
        Jitotal = jitotal;
    }

    @SerializedName(value = "total", alternate = {"vod_total"})
    private String total;//总集数

    @SerializedName(value = "serial", alternate = {"vod_serial"})
    private String serial;//更新至

    private int Jitotal;//实际解析得到的集数

    protected Bangumi(Parcel in) {
        vod_id = in.readInt();
        soure = in.readString();
        name = in.readString();
        cover = in.readString();
        img = in.readString();
        intro = in.readString();
        hits = in.readString();
        remarks = in.readString();
        total = in.readString();
        serial = in.readString();
    }

    public static final Creator<Bangumi> CREATOR = new Creator<Bangumi>() {
        @Override
        public Bangumi createFromParcel(Parcel in) {
            return new Bangumi(in);
        }

        @Override
        public Bangumi[] newArray(int size) {
            return new Bangumi[size];
        }
    };

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

    public Bangumi() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vod_id);
        dest.writeString(soure);
        dest.writeString(name);
        dest.writeString(cover);
        dest.writeString(img);
        dest.writeString(intro);
        dest.writeString(hits);
        dest.writeString(remarks);
        dest.writeString(total);
        dest.writeString(serial);
    }
}
