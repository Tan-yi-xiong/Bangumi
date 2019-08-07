package com.TyxApp.bangumi.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.TyxApp.bangumi.util.LogUtil;
import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "BANGUMI",
        indices = @Index(value = {"vod_id", "vod_soure"}, unique = true))
public class Bangumi implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int dbId;

    @SerializedName(value = "id", alternate = {"vod_id", "typeid"})
    @ColumnInfo(name = "vod_id")
    private int vodId;

    @ColumnInfo(name = "vod_soure")
    private String videoSoure;//来源

    @SerializedName(value = "name", alternate = {"vod_name", "typename", "source"})
    private String name;

    @SerializedName(value = "pic", alternate = {"vod_pic", "litpic", "suoluetudizhi"})
    private String cover;

    private String img;

    @SerializedName(value = "hits", alternate = {"vod_hits", "click"})
    @Ignore
    private String hits;//热度

    @SerializedName(value = "remarks", alternate = {"vod_remarks"})
    private String remarks;

    private boolean isFavorite;//是否设置为追番

    private boolean isDownLoad;//是否有下载


    @SerializedName(value = "total", alternate = {"vod_total", "latest"})
    private String total;//总集数

    @SerializedName(value = "serial", alternate = {"vod_serial", "ji", "writer"})
    private String serial;//更新至

    private long historyTime;//点击番剧观看的时间戳, 历史观看使用;

    @Ignore
    private BangumiInfo mBangumiInfo;

    public BangumiInfo getBangumiInfo() {
        return mBangumiInfo;
    }

    public void setBangumiInfo(BangumiInfo bangumiInfo) {
        mBangumiInfo = bangumiInfo;
        if (!TextUtils.isEmpty(bangumiInfo.getJiTotal())) {
            remarks = bangumiInfo.getJiTotal();
        }
    }

    protected Bangumi(Parcel in) {
        vodId = in.readInt();
        videoSoure = in.readString();
        name = in.readString();
        cover = in.readString();
        img = in.readString();
        hits = in.readString();
        remarks = in.readString();
        total = in.readString();
        serial = in.readString();
    }

    public String getIntro() {
        return mBangumiInfo == null ? null : mBangumiInfo.getIntro();
    }


    public String getNiandai() {
        return mBangumiInfo == null ? null : mBangumiInfo.getNiandai();
    }


    public String getCast() {
        return mBangumiInfo == null ? null : mBangumiInfo.getCast();
    }


    public String getStaff() {
        return mBangumiInfo == null ? null : mBangumiInfo.getStaff();
    }


    public String getType() {
        return mBangumiInfo == null ? null : mBangumiInfo.getType();
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isDownLoad() {
        return isDownLoad;
    }

    public void setDownLoad(boolean downLoad) {
        isDownLoad = downLoad;
    }

    public long getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(long historyTime) {
        this.historyTime = historyTime;
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
        this.vodId = vod_id;
        this.videoSoure = soure;
        this.name = name;
        this.cover = cover;
    }

    public int getVodId() {
        return vodId;
    }

    public void setVodId(int vodId) {
        this.vodId = vodId;
    }

    public String getVideoSoure() {
        return videoSoure;
    }

    public void setVideoSoure(String videoSoure) {
        this.videoSoure = videoSoure;
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


    public String getLatestJi () {
        String jiTotal = remarks;
        if (TextUtils.isEmpty(jiTotal)) {
            StringBuilder builder = new StringBuilder();
            jiTotal = total;
            if (!TextUtils.isEmpty(jiTotal) && !"0".equals(jiTotal)) {
                builder.append("全");
                builder.append(jiTotal);
                builder.append("话");
            } else if (!TextUtils.isEmpty(serial)){
                builder.append("更新至");
                builder.append(serial);
                if (!serial.contains("话")) {
                    builder.append("话");
                }
            }
            jiTotal = builder.toString();
        }

        return jiTotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vodId);
        dest.writeString(videoSoure);
        dest.writeString(name);
        dest.writeString(cover);
        dest.writeString(img);
        dest.writeString(hits);
        dest.writeString(remarks);
        dest.writeString(total);
        dest.writeString(serial);
    }
}
