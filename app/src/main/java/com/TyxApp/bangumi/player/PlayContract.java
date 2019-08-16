package com.TyxApp.bangumi.player;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;
import com.TyxApp.bangumi.data.bean.VideoUrl;

import java.util.List;

public interface PlayContract {
    interface Presenter extends BasePresenter {
        void getBangumiInfo(String bangumiId);

        void getBangumiJiList(String bangumiId);

        void getPlayerUrl(String id, int ji);

        void getRecommendBangumis(String id);

        void checkFavorite(String id, String sourch);

        void setFavorite(Bangumi bangumi);

        void setTime(Bangumi bangumi);

        void setDownload(Bangumi bangumi);
    }

    interface View {
        void showBangumiInfo(BangumiInfo info);

        void showBangumiJiList(List<TextItemSelectBean> jiList);

        void setPlayerUrl(VideoUrl url);

        void showRecommendBangumis(List<Bangumi> recommendBangumis);

        void showFavoriteButton(boolean isFavourite);

    }
}
