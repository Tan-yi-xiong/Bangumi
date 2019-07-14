package com.TyxApp.bangumi.data;

public interface VideoPlayerEvent {
    interface Key {
        String ISFULLSCREENKEY = "full_screen_key";
    }

    interface Code {
        int CODE_FULL_SCREEN = 88;
        int CODE_BACK = 99;
    }
}
