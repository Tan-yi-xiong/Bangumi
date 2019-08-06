package com.TyxApp.bangumi.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ActivityUtil {
    public static void addFragmentToActivity(FragmentManager manager, Fragment fragment, int contentId) {
        manager.beginTransaction()
                .add(contentId, fragment, fragment.getClass().getName())
                .commit();
    }

    public static void replaceFragment(FragmentManager manager, Fragment fragment, int contentId) {
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(contentId, fragment, fragment.getClass().getName())
                .commit();
    }

    public static Fragment findFragment(FragmentManager manager, String tag) {
        return manager.findFragmentByTag(tag);
    }

    public static void addFragmentToActivity(FragmentManager manager, Fragment fragment, int contentId, int transition) {
        manager.beginTransaction()
                .setTransition(transition)
                .add(contentId, fragment, fragment.getClass().getName())
                .commit();
    }

    public static void addFragmentToBackTask(FragmentManager manager, Fragment fragment, int contentId, String name) {
        manager.beginTransaction()
                .add(contentId, fragment, fragment.getClass().getName())
                .addToBackStack(name)
                .commit();
    }

    public static void addFragmentToBackTask(FragmentManager manager, Fragment fragment, int contentId, int transition, String name) {
        manager.beginTransaction()
                .setTransition(transition)
                .add(contentId, fragment, fragment.getClass().getName())
                .addToBackStack(name)
                .commit();
    }
}
