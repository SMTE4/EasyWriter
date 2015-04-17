package com.kargathia.easywriter.Drawing;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.kargathia.easywriter.Conversations.ConversationDisplay;

/**
 * Created by Kargathia on 04/04/2015.
 */
public class ActivitySwipeDetector implements View.OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private ConversationDisplay activity;
    private View listeningTo, animateThis;
    static final int MIN_DISTANCE = 100;
    private float
            downX, upX,
            translation;
    private boolean isMoving = false;
    private TranslateAnimation slideAnimation;

    public ActivitySwipeDetector(ConversationDisplay activity, View listeningTo, View animateThis) {
        this.activity = activity;
        this.listeningTo = listeningTo;
        this.animateThis = animateThis;
    }

    public void onRightToLeftSwipe() {
        Log.i(logTag, "RightToLeftSwipe!");
        activity.backGesture();
    }

    public void onLeftToRightSwipe() {
        Log.i(logTag, "LeftToRightSwipe!");
        activity.acceptGesture();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();

                if (listeningTo instanceof Button) {
                    ((Button) listeningTo).setPressed(true);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                final float dx = x - downX;

                // start slide animation
                if (!isMoving && Math.abs(dx) > MIN_DISTANCE) {
                    isMoving = true;
                    Log.i("animation", "moving now");
                    if (dx > 0) {
                        translation = 300;
                    } else if (dx < 0) {
                        translation = -300;
                    }
                    slideAnimation = new TranslateAnimation(0, translation, 0, 0);
                    slideAnimation.setFillAfter(true);
                    slideAnimation.setDuration(500);
                    animateThis.startAnimation(slideAnimation);

                    if (listeningTo instanceof Button) {
                        ((Button) listeningTo).setPressed(false);
                    }
                }
                // when reversing a viable swipe
                if (isMoving && (dx * translation < 0) && slideAnimation.hasEnded()) {
                    slideAnimation = new TranslateAnimation(translation, -translation, 0, 0);
                    translation = -translation;
                    slideAnimation.setFillAfter(true);
                    slideAnimation.setDuration(800);
                    animateThis.startAnimation(slideAnimation);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                float deltaX = downX - upX;

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
                        ((Button) listeningTo).setPressed(false);
                    }
                }

                // reset slide animation to starting position
                if (isMoving) {
                    isMoving = false;
                    Log.i("animation", "moving back");
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
