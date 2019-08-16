package com.TyxApp.bangumi.util;

import android.transition.Transition;
import android.transition.TransitionSet;

public class TransitionUtils {

    public static Transition findTransition(TransitionSet transitionSet, Class clzz, int targetId) {
        for (int i = 0; i < transitionSet.getTransitionCount(); i++) {
            Transition transition = transitionSet.getTransitionAt(i);
            if (transition.getClass() == clzz) {
                if (transition.getTargetIds().contains(targetId)) {
                    return transition;
                }
            }
            if (transition instanceof TransitionSet) {
                return findTransition((TransitionSet) transition, clzz, targetId);
            }
        }
        return null;
    }

    public static class TransitionListenerAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }
}
