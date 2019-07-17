package com.TyxApp.bangumi.player;

import com.TyxApp.bangumi.base.BasePresenter;
import com.TyxApp.bangumi.data.Bangumi;
import com.TyxApp.bangumi.data.TextItemSelectBean;

import java.util.List;

public interface PlayContract {
    interface Presenter extends BasePresenter {
        void getBangumiIntro(int bangumiId);

        void getBangumiJiList(int bangumiId);

        void getPlayerUrl(int id, int ji);

        void getRecommendBangumis(int id);
    }

    interface View {
        void showBangumiIntro(String intor);

        void showBangumiJiList(List<TextItemSelectBean> jiList);

        void setPlayerUrl(String url);

        void showRecommendBangumis(List<Bangumi> recommendBangumis);

        void showError(Throwable throwable);
    }
}
