package com.TyxApp.bangumi.player;

import androidx.annotation.Nullable;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;

import java.util.List;

import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

public interface PlayContract {
    interface Presenter extends BasePresenter {
        void getBangumiInfo(String bangumiId);

        void getBangumiJiList(String bangumiId);

        void getPlayerUrl(String id, int ji);

        void getDanmaku(String id, int ji);

        void getRecommendBangumis(String id);

        void checkFavorite(String id, String sourch);

        void setFavorite(Bangumi bangumi);

        void setTime(Bangumi bangumi);

        void setDownload(Bangumi bangumi);
    }

    interface View {
        void showBangumiInfo(@Nullable BangumiInfo info);

        void showBangumiJiList(@Nullable List<TextItemSelectBean> jiList);

        void setPlayerUrl(@Nullable VideoUrl url);

        void showRecommendBangumis(@Nullable List<Bangumi> recommendBangumis);

        void showFavoriteButton(boolean isFavourite);

        void setDanmaku(@Nullable BaseDanmakuParser danmakuParser);

    }
}
