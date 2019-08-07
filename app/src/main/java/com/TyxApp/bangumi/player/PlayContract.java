package com.TyxApp.bangumi.player;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.base.BaseView;
import com.TyxApp.bangumi.data.bean.Bangumi;
import com.TyxApp.bangumi.data.bean.BangumiInfo;
import com.TyxApp.bangumi.data.bean.TextItemSelectBean;

import java.util.List;

public interface PlayContract {
    interface Presenter extends BasePresenter {
        void getBangumiInfo(int bangumiId);

        void getBangumiJiList(int bangumiId);

        void getPlayerUrl(int id, int ji);

        void getRecommendBangumis(int id);

        void isFavorite(int id, String sourch);

        void setFavorite (Bangumi bangumi);

        void setTime(Bangumi bangumi);

        void setDownload(Bangumi bangumi);
    }

    interface View extends BaseView {
        void showBangumiInfo(BangumiInfo info);

        void showBangumiJiList(List<TextItemSelectBean> jiList);

        void setPlayerUrl(String url);

        void showRecommendBangumis(List<Bangumi> recommendBangumis);


        void changeFavoriteButtonState(boolean isFavourite);

        void showSkipDialog(String url);
    }
}
