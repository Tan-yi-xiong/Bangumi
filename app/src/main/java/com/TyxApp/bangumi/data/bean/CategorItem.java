package com.TyxApp.bangumi.data.bean;

import com.google.gson.annotations.SerializedName;

public class CategorItem {
    @SerializedName(value = "suoluetudizhi")
    private Object imageRes;
    @SerializedName(value = "typename")
    private String name;

    public CategorItem(Object imageRes, String name) {
        this.imageRes = imageRes;
        this.name = name;
    }

    public CategorItem() {
    }

    public Object getImageRes() {
        return imageRes;
    }

    public void setImageRes(Object imageRes) {
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
