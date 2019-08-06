package com.TyxApp.bangumi.data.bean;

import java.util.List;

public class Results {
    private boolean finalTag;
    private List<Bangumi> mBangumis;

    public Results(boolean finalTag, List<Bangumi> bangumis) {
        this.finalTag = finalTag;
        mBangumis = bangumis;
    }

    public boolean isFinalTag() {
        return finalTag;
    }

    public void setFinalTag(boolean finalTag) {
        this.finalTag = finalTag;
    }

    public List<Bangumi> getBangumis() {
        return mBangumis;
    }

    public void setBangumis(List<Bangumi> bangumis) {
        mBangumis = bangumis;
    }
}
