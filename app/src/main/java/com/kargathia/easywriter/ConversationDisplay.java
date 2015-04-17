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

    private RelativeLayout
            layoutMessageButtons,
            layoutHistory;
    private TextView
            tvDrawPrompt,
            tvLetterDisplay,
            ph_tvMessageDisplay;
    private DrawingView dvDrawDisplay;
    private Button
            btnSendMessage,
            btnBack,
            btnAccept;
    private int number = 0;
    private Contact contact = null;
    private ContactProvider provider = ContactProvider.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_display);

        TextView tvContactName = (TextView) this.findViewById(R.id.tvContactName);

        Intent intent = getIntent();
        number = intent.getIntExtra("ContactPosition", -1);
        if (number != -1) {
            for (Contact x : provider.getSmsContacten()) {
                if (x.getID() == number) {
                    contact = x;
                }
            }
            tvContactName.setText(contact.getName());
        }

//        this.tfTest = (EditText) this.findViewById(R.id.tfTestField);
        this.tvDrawPrompt = (TextView) this.findViewById(R.id.stat_tvDrawPrompt);
        this.dvDrawDisplay = (DrawingView) this.findViewById(R.id.dvDrawDisplay);
        this.ph_tvMessageDisplay = (TextView) this.findViewById(R.id.ph_tvMessageDisplay);
        this.tvLetterDisplay = (TextView) this.findViewById(R.id.tvLetterDisplay);
//        this.tvSwipeRightPrompt = (TextView) this.findViewById(R.id.stat_tvRightSwipePrompt);

        dvDrawDisplay.setOutput(tvLetterDisplay);

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
        this.btnSendMessage = (Button) this.findViewById(R.id.btnSendMessage);
        this.btnBack = (Button) this.findViewById(R.id.btnBackMessage);
        this.btnAccept = (Button) this.findViewById(R.id.btnAcceptMessage);
        this.layoutHistory = (RelativeLayout) this.findViewById(R.id.layoutHistory);


//        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
        layoutMessageButtons = (RelativeLayout) this.findViewById(R.id.layoutMessageButtons);
//        layoutMessageButtons.setOnTouchListener(activitySwipeDetector);

        layoutHistory.setOnTouchListener(new ActivitySwipeDetector(this, layoutHistory, layoutMessageButtons));
        btnAccept.setOnTouchListener(new ActivitySwipeDetector(this, btnAccept, layoutMessageButtons));
        btnSendMessage.setOnTouchListener(new ActivitySwipeDetector(this, btnSendMessage, layoutMessageButtons));
        btnBack.setOnTouchListener(new ActivitySwipeDetector(this, btnBack, layoutMessageButtons));

        // send message
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast("sending message: " + ph_tvMessageDisplay.getText());
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptGesture();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backGesture();
            }
        });

    }

    public void backGesture() {
        String text = ph_tvMessageDisplay.getText().toString();
        int length = text.length();
        if (dvDrawDisplay.resetCanvas()) {
            displayToast("wiped");
            return;
        } else if (length > 0) {
            text = text.substring(0, length - 1);
            ph_tvMessageDisplay.setText(text);
        } else {
            displayToast("going back now");
        }
    }

    public void acceptGesture() {
        String letter = dvDrawDisplay.getRecognisedText();
//        displayToast("adding letter: " + );
        ph_tvMessageDisplay.append(letter);
        dvDrawDisplay.resetCanvas();
    }

    private void displayToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
