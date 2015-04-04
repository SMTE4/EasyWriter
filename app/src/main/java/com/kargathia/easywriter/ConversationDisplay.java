package com.kargathia.easywriter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class ConversationDisplay extends Activity {

    private RelativeLayout layoutHistory;
    private EditText tfTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_display);

        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
        layoutHistory = (RelativeLayout) this.findViewById(R.id.layoutHistory);
        layoutHistory.setOnTouchListener(activitySwipeDetector);

        this.tfTest = (EditText) this.findViewById(R.id.tfTestField);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversation_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setOnClicks() {

    }

    public void startWriting() {

    }

    public void letterOK() {

    }

    public void backGesture() {
        int length = tfTest.getText().length();
        if (length > 0) {
//            tfTest.getText().delete(length - 1, length);
            tfTest.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            tfTest.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        } else {
            displayToast("going back now");
        }
    }

    public void acceptGesture() {
        displayToast("sending message: " + tfTest.getText().toString());
    }

    private void displayToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
