package com.TyxApp.bangumi.data;

public class StackBangumi {
    private int PlayedJi;
    private Bangumi mBangumi;

    public StackBangumi(int playedJi, Bangumi bangumi) {
        PlayedJi = playedJi;
        mBangumi = bangumi;
    }

    public int getPlayedJi() {
        return PlayedJi;
    }

    public void setPlayedJi(int playedJi) {
        PlayedJi = playedJi;
    }

    public Bangumi getBangumi() {
        return mBangumi;
    }

    public void setBangumi(Bangumi bangumi) {
        mBangumi = bangumi;
    }
}
