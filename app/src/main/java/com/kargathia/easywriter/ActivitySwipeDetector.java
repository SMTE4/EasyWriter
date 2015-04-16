package com.kargathia.easywriter;

import android.app.Activity;
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
    private ConversationDisplay activity;
    private View listeningTo, animateThis;
    static final int MIN_DISTANCE = 100;
    private float
            downX, downY,
            upX, upY,
            lastTouchX,
            viewPosX;

    private TranslateAnimation transAnim;

    public ActivitySwipeDetector(ConversationDisplay activity, View listeningTo, View animateThis) {
        this.activity = activity;
        this.listeningTo = listeningTo;
        this.animateThis = animateThis;
        viewPosX = animateThis.getX();
        Log.i("default translation", String.valueOf(animateThis.getTranslationX()));
    }

    public void onRightToLeftSwipe() {
        Log.i(logTag, "RightToLeftSwipe!");
        activity.backGesture();
    }

    public void onLeftToRightSwipe() {
        Log.i(logTag, "LeftToRightSwipe!");
        activity.acceptGesture();
    }

    public void onTopToBottomSwipe() {
        Log.i(logTag, "onTopToBottomSwipe!");
//        activity.doSomething();
    }

    public void onBottomToTopSwipe() {
        Log.i(logTag, "onBottomToTopSwipe!");
//        activity.doSomething();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();

                lastTouchX = downX;
//                lastTouchY = downY;
//                viewPosX = animateThis.getX();

                if(listeningTo instanceof Button){
                    ((Button)listeningTo).setPressed(true);
                }

                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                final float x = event.getX();
//                final float y = event.getY();

                // Calculate the distance moved
                final float dx = x - lastTouchX;
//                final float dy = y - lastTouchY;

//                animateThis.animate()
//                        .translationX(x - downX)
//                        .setDuration(200);

                // Move the object
                animateThis.setTranslationX(x - downX);
//                animateThis.setTranslationY(animateThis.getTranslationY() + dy);

                // Remember this touch position for the next move event
                lastTouchX = x;
//                lastTouchY = y;

                // Invalidate to request a redraw
                animateThis.invalidate();
                break;

            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                try {
                    // swipe horizontal?
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // left or right
                            if (deltaX < 0) {
                                this.onLeftToRightSwipe();
                                return true;
                            }
                            if (deltaX > 0) {
                                this.onRightToLeftSwipe();
                                return true;
                            }
                        } else {
                            Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }
                    // swipe vertical?
                    else {
                        if (Math.abs(deltaY) > MIN_DISTANCE) {
                            // top or down
                            if (deltaY < 0) {
                                this.onTopToBottomSwipe();
                                return true;
                            }
                            if (deltaY > 0) {
                                this.onBottomToTopSwipe();
                                return true;
                            }
                        } else {
                            Log.i(logTag, "Vertical Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }
                } finally {
                    if (listeningTo instanceof Button) {
                        listeningTo.performClick();
                        ((Button) listeningTo).setPressed(false);
                    }
//                    animateThis.animate().translationX(0).setDuration(200);
                    animateThis.setTranslationX(0);
                    animateThis.invalidate();
//                    TranslateAnimation returnTranslation = new TranslateAnimation()
                }
                return true;
            }
        }
        return false;
    }

}
