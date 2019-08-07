package com.TyxApp.bangumi.data.bean;

import com.google.gson.annotations.SerializedName;

public class BangumiInfo {
    private String niandai;//年份

    private String cast;//声优

    private String staff;//制作

    @SerializedName(value = "biaoqian")
    private String type;//类型

    @SerializedName(value = "zhuangtai")
    private String jiTotal;//集数

    @SerializedName(value = "description")
    private String intro;//简介

    public BangumiInfo(String niandai, String cast, String staff, String type, String intro, String jiTotal) {
        this.niandai = niandai;
        this.cast = cast;
        this.jiTotal = jiTotal;
        this.staff = staff;
        this.type = type;
        this.intro = intro;
    }

    public String getJiTotal() {
        return jiTotal;
    }

    public void setJiTotal(String jiTotal) {
        this.jiTotal = jiTotal;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNiandai() {
        return niandai;
    }

    public void setNiandai(String niandai) {
        this.niandai = niandai;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
