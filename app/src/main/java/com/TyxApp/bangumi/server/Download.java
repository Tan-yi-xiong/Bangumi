package com.TyxApp.bangumi.server;

import com.TyxApp.bangumi.data.bean.Bangumi;

public interface Download {
    void addStack(Bangumi bangumi, String videoUrl, String fileName);

    void start();

    void pause(String url);

}
