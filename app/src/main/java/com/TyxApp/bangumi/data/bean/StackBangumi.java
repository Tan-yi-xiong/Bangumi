package com.TyxApp.bangumi.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class StackBangumi implements Parcelable {
    private List<TextItemSelectBean> PlayedJi;
    private Bangumi mBangumi;
    private List<Bangumi> recommendBangumis;
    private int currentJi;
    private int jiTotal;

    protected StackBangumi(Parcel in) {
        mBangumi = in.readParcelable(Bangumi.class.getClassLoader());
        recommendBangumis = in.createTypedArrayList(Bangumi.CREATOR);
        currentJi = in.readInt();
        jiTotal = in.readInt();
        playingUrl = in.readString();
    }

    public static final Creator<StackBangumi> CREATOR = new Creator<StackBangumi>() {
        @Override
        public StackBangumi createFromParcel(Parcel in) {
            return new StackBangumi(in);
        }

        @Override
        public StackBangumi[] newArray(int size) {
            return new StackBangumi[size];
        }
    };

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

    public String getBangumiId() {
        return mBangumi.getVideoId();
    }

    public String getBanhumiSourch() {
        return mBangumi.getVideoSoure();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mBangumi, flags);
        dest.writeTypedList(recommendBangumis);
        dest.writeInt(currentJi);
        dest.writeInt(jiTotal);
        dest.writeString(playingUrl);
    }
}
