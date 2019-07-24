package com.TyxApp.bangumi.data.bean;

import java.util.List;

public class StackBangumi {
    private List<TextItemSelectBean> PlayedJi;
    private Bangumi mBangumi;
    private List<Bangumi> recommendBangumis;
    private int currentJi;
    private int jiTotal;

    public int getJiTotal() {
        return jiTotal;
    }

    public void setJiTotal(int jiTotal) {
        this.jiTotal = jiTotal;
    }

    public String getPlayingUrl() {
        return playingUrl;
    }

    public void setPlayingUrl(String playingUrl) {
        this.playingUrl = playingUrl;
    }

    private String playingUrl;

    public int getCurrentJi() {
        return currentJi;
    }

    public void setCurrentJi(int currentJi) {
        this.currentJi = currentJi;
    }

    public StackBangumi(Bangumi bangumi) {
        mBangumi = bangumi;
    }

    public List<Bangumi> getRecommendBangumis() {
        return recommendBangumis;
    }

    public void setRecommendBangumis(List<Bangumi> recommendBangumis) {
        this.recommendBangumis = recommendBangumis;
    }

    public List<TextItemSelectBean> getPlayedJi() {
        return PlayedJi;
    }

    public void setPlayedJi(List<TextItemSelectBean> playedJi) {
        PlayedJi = playedJi;
    }

    public Bangumi getBangumi() {
        return mBangumi;
    }

    public void setBangumi(Bangumi bangumi) {
        mBangumi = bangumi;
    }

    public boolean isLastJi() {
        return currentJi == jiTotal - 1;
    }

    public void nextJi() {
        currentJi++;
    }

    public String getBangumiName() {
        return mBangumi.getName();
    }

    public int getBangumiId() {
        return mBangumi.getVodId();
    }

    public String getBanhumiSourch() {
        return mBangumi.getVideoSoure();
    }
}
