package com.juny.junymusic.util;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015-04-23.
 */
public abstract class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_VELOCITY_THRESHOLD = 200;

    public OnSwipeListener() {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // y값을 너무 많이 움직였으면 아무것도 하지 않음
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;

        if (Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if ((e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE) {
                onSwipeleft();
            }
            else if ((e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE)) {
                onSwipeRight();
            }
        }
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return onClick(e);
    }

    public abstract boolean onSwipeleft();
    public abstract boolean onSwipeRight();
    public abstract boolean onClick(MotionEvent event);
}
