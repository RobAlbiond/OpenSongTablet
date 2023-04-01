package com.garethevans.church.opensongtablet.controls;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private final int swipeMinimumDistance, swipeMaxDistanceYError, swipeMinimumVelocity;
    private final MainActivityInterface mainActivityInterface;
    private boolean doubleTapping;
    @SuppressWarnings({"unused","FieldCanBeLocal"})
    private final String TAG = "GestureListener";

    public GestureListener(MainActivityInterface mainActivityInterface,
                           int swipeMinimumDistance, int swipeMaxDistanceYError, int swipeMinimumVelocity) {
        this.mainActivityInterface = mainActivityInterface;
        this.swipeMinimumDistance = swipeMinimumDistance;
        this.swipeMaxDistanceYError = swipeMaxDistanceYError;
        this.swipeMinimumVelocity = swipeMinimumVelocity;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        doubleTapping = true;
        return performAction(mainActivityInterface.getGestures().getDoubleTap());
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG,"onLongPress");
        super.onLongPress(e);
        if (doubleTapping) {
            doubleTapping = false;
        } else {
            performAction(mainActivityInterface.getGestures().getLongPress());
        }
    }

    private boolean performAction(String whichAction) {
        // Send the gesture to the Performance gestures to run
        // isLongPress is for page button, not the long press gesture!
        mainActivityInterface.getPerformanceGestures().doAction(whichAction,false);
        return true;
    }

    // The listener for swiping between songs
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDistanceYError) {
                return false;

            } else if (mainActivityInterface.getGestures().getSwipeEnabled() &&
                    e1.getX() - e2.getX() > swipeMinimumDistance
                    && Math.abs(velocityX) > swipeMinimumVelocity) {
                mainActivityInterface.getDisplayPrevNext().setSwipeDirection("R2L");
                mainActivityInterface.getDisplayPrevNext().moveToNext();
                return true;

            } else if (mainActivityInterface.getGestures().getSwipeEnabled() &&
                    e2.getX() - e1.getX() > swipeMinimumDistance
                    && Math.abs(velocityX) > swipeMinimumVelocity) {
                mainActivityInterface.getDisplayPrevNext().setSwipeDirection("L2R");
                mainActivityInterface.getDisplayPrevNext().moveToPrev();
                return true;

            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
