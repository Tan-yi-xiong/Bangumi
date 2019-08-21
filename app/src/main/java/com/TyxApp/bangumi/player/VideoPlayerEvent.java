package com.TyxApp.bangumi.player;

public interface VideoPlayerEvent {
    interface DECODE_PLAN {
      int PLAN_ID_IJK = 1;

      String PLAN_NAME_IJK = "IjkPlayer";
      String PLAN_NAME_MEDIA = "MediaPlayer";
    }

    interface Key {
        String IS_FULLSCREEN_KEY = "full_screen_key";
        String NOTIFI_ERROR_COVER_SHOW = "N_E_C_S";
        String CONTROL_VIEW_SHOW = "c_v_s";
        String ERROR_COVER_SHOW = "e_c_s";
        String DANMAKU_VISIBLE = "d_v";
    }

    interface Code {
        int CODE_FULL_SCREEN = 88;
        int CODE_BACK = 99;
        int CODE_NEXT = 333;
        int CODE_SYNC_PLAY_TIME = 555;
        int CODE_ERROR_COVER_SHOW = 666;
        int CODE_CONTROL_VIEW_SHOW = 777;
        int CODE_PLAYER_MORE_CLICK = 888;
        int CODE_DANMAKU_PREPARED = 999;
    }
}
