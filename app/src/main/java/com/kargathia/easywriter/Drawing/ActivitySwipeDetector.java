package com.kargathia.easywriter.Drawing;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

/**
 * Created by Kargathia on 04/04/2015.
 */
public class ActivitySwipeDetector implements View.OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private IActivitySwipeInterpreter activity;
    private View
            listeningTo,
            animateThis;
    static final int MIN_DISTANCE = 100;
    private float
            downX, upX,
            translation;
    private boolean isMoving = false;
    private TranslateAnimation slideAnimation;

    /**
     * @param activity    handling the gestures recognised
     * @param listeningTo view currently being watched - used to relaying click events
     * @param animateThis view that should be animated to provide feedback. Can feature in multiple
     *                    ActivitySwipeDetectors
     */
    public ActivitySwipeDetector(IActivitySwipeInterpreter activity, View listeningTo, View animateThis) {
        this.activity = activity;
        this.listeningTo = listeningTo;
        this.animateThis = animateThis;
    }

    private void onRightToLeftSwipe() {
        Log.i(logTag, "RightToLeftSwipe!");
        activity.backGesture();
    }

    private void onLeftToRightSwipe() {
        Log.i(logTag, "LeftToRightSwipe!");
        activity.acceptGesture();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            // Finger starts touching the screen
            // Start of MotionEvent
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();

                // A click is a specific OnTouch event
                // If buttons are not set to Pressed state, they will not visually register clicks
                if (listeningTo instanceof Button) {
                    (listeningTo).setPressed(true);
                }
                break;
            }

            // Finger moves. Will get called multiple times if one swipes across the screen
            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                final float dx = x - downX;

                // start slide animation if it hasn't already
                // Animation will move to end of screen, and stop there.
                // A separate animation is started on ACTION_UP to reverse this
                if (!isMoving && Math.abs(dx) > MIN_DISTANCE) {
                    isMoving = true;
                    Log.i(logTag, "moving now");
                    if (dx > 0) {
                        translation = 300;
                    } else if (dx < 0) {
                        translation = -300;
                    }
                    slideAnimation = new TranslateAnimation(0, translation, 0, 0);
                    slideAnimation.setFillAfter(true);
                    slideAnimation.setDuration(500);
                    animateThis.startAnimation(slideAnimation);

                    // If user is moving more than MIN_DISTANCE, it obviously is not a button press
                    if (listeningTo instanceof Button) {
                        (listeningTo).setPressed(false);
                    }
                }
                // When reversing a viable swipe (User swipes to right side, then completely to left)
                // Functionally this is a swipe left -> animation needs to follow
                if (isMoving && (dx * translation < 0) && slideAnimation.hasEnded()) {
                    slideAnimation = new TranslateAnimation(translation, -translation, 0, 0);
                    translation = -translation;
                    slideAnimation.setFillAfter(true);
                    slideAnimation.setDuration(800);
                    animateThis.startAnimation(slideAnimation);
                }
                break;
            }

            // Finger releases the screen. End of MotionEvent
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                float deltaX = downX - upX;

                // Calls functional results - gesture commands or clicks
                // MotionEvent is consumed, so button is clicked programmatically if event was no swipe
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                    }
                } else {
                    Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX)
                            + " long, need at least " + MIN_DISTANCE);
                    if (listeningTo instanceof Button) {
                        listeningTo.performClick();
                        (listeningTo).setPressed(false);
                    }
                }

                // reset slide animation to starting position
                if (isMoving) {
                    isMoving = false;
                    Log.i(logTag, "moving back");
                    slideAnimation = new TranslateAnimation(translation, 0, 0, 0);
                    slideAnimation.setFillAfter(true);
                    slideAnimation.setDuration(100);
                    animateThis.startAnimation(slideAnimation);
                }
            }
        }
        return true;
    }

}
