package com.kargathia.easywriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ConversationDisplay extends Activity {

    private RelativeLayout layoutHistory;
    private TextView
            tvDrawPrompt,
            tvSwipeRightPrompt;
    private DrawingView dvDrawDisplay;
    private Button btnSendMessage;
    private int number = 0;
    private Contact contact = null;
    private ContactProvider provider = ContactProvider.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_display);

        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
        layoutHistory = (RelativeLayout) this.findViewById(R.id.layoutHistory);
        layoutHistory.setOnTouchListener(activitySwipeDetector);

        TextView tv = (TextView)this.findViewById(R.id.tvContactName);

        Intent intent = getIntent();
        number = intent.getIntExtra("ContactPosition", -1);
        if(number != -1){
            System.out.println(number);
            contact = provider.contacten.get(number);
            tv.setText(contact.name);
        }

//        this.tfTest = (EditText) this.findViewById(R.id.tfTestField);
        this.tvDrawPrompt = (TextView) this.findViewById(R.id.stat_tvDrawPrompt);
        this.dvDrawDisplay = (DrawingView) this.findViewById(R.id.dvDrawDisplay);
        this.btnSendMessage = (Button) this.findViewById(R.id.btnSendMessage);
        this.tvSwipeRightPrompt = (TextView) this.findViewById(R.id.stat_tvRightSwipePrompt);

        dvDrawDisplay.setOutput(tvSwipeRightPrompt);

        this.setOnClicks();
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
        // send message
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("sending message: " + tvDrawPrompt.getText());
            }
        });
    }

    public void backGesture() {
        String text = tvDrawPrompt.getText().toString();
        int length = text.length();
        if(dvDrawDisplay.resetCanvas()){
            displayToast("wiped");
            return;
        } else if (length > 0) {
            text = text.substring(0, length - 1);
            tvDrawPrompt.setText(text);
        } else {
            displayToast("going back now");
        }
    }

    public void acceptGesture() {
        displayToast("adding letter: " + dvDrawDisplay.getRecognisedText());
        dvDrawDisplay.resetCanvas();
    }

    private void displayToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
