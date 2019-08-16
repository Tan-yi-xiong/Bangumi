package com.TyxApp.bangumi.player;

public interface VideoPlayerEvent {
    interface DECODE_PLAN {
      int PLAN_ID_IJK = 1;

      String PLAN_NAME_IJK = "IjkPlayer";
      String PLAN_NAME_MEDIA = "MediaPlayer";
    }

    interface Key {
        String IS_FULLSCREEN_KEY = "full_screen_key";
        String SPEED_DATA_KEY = "S_D_k";
        String NOTIFI_ERROR_COVER_SHOW = "N_E_C_S";
        String CONTROL_VIEW_SHOW = "c_v_s";
    }

    interface Code {
        int CODE_FULL_SCREEN = 88;
        int CODE_BACK = 99;
        int CODE_SPEED_CHANGE = 111;
        int CODE_DOWNLOAD = 222;
        int CODE_NEXT = 333;
        int CODE_SYNC_PLAY_TIME = 555;
        int CODE_ERROR_COVER_SHOW = 666;
        int CODE_CONTROL_VIEW_SHOW = 777;
        int CODE_PLAYER_MORE_CLICK = 888;
    }
}
