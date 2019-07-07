package com.TyxApp.bangumi.mainpage.homecontent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class BangumiFragment extends Fragment {
    public static BangumiFragment newInstance() {
        
        Bundle args = new Bundle();
        
        BangumiFragment fragment = new BangumiFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
