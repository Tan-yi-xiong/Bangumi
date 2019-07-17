package com.TyxApp.bangumi.data;

public interface VideoPlayerEvent {
    interface Key {
        String IS_FULLSCREEN_KEY = "full_screen_key";
        String SPEED_DATA_KEY = "S_D_k";
        String NOTIFI_ERROR_COVER_SHOW = "N_E_C_S";
    }

    interface Code {
        int CODE_FULL_SCREEN = 88;
        int CODE_BACK = 99;
        int CODE_SPEED_CHANGE = 111;
        int CODE_DOWNLOAD = 222;
        int CODE_NEXT = 333;
        int CODE_BRIGHTNESS_ADJUST = 444;
        int CODE_SYNC_PLAY_TIME = 555;
        int CODE_ERROR_COVER_SHOW = 666;
    }
}
