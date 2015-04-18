package com.kargathia.easywriter.Conversations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kargathia.easywriter.Contacts.Contact;
import com.kargathia.easywriter.Contacts.ContactProvider;
import com.kargathia.easywriter.Drawing.ActivitySwipeDetector;
import com.kargathia.easywriter.Drawing.DrawingView;
import com.kargathia.easywriter.Drawing.IActivitySwipeInterpreter;
import com.kargathia.easywriter.R;


public class ConversationDisplay extends Activity implements IActivitySwipeInterpreter {

    public static String
            INTENT_CONTACT_ID = "com.kargathia.easywriter.contactID";

    private RelativeLayout
            layoutMessageButtons,
            layoutHistory,
            layoutDrawBoard;
    private TextView
            tvDrawPrompt,
            tvLetterDisplay,
            tvContactName;
    private TextView ph_tvMessageDisplay;
    private DrawingView dvDrawDisplay;
    private Button
            btnSendMessage,
            btnBack,
            btnAccept;
    private Contact contact = null;

    /**
     * Starts a ConversationDisplay from given context
     *
     * @param context
     * @param contactIndex
     */
    public static void startNew(Context context, int contactIndex) {
        Intent intent = new Intent(context, ConversationDisplay.class);
        intent.putExtra(INTENT_CONTACT_ID, contactIndex);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_display);

        this.initViews();

        Intent intent = getIntent();
        int contactID = intent.getIntExtra(INTENT_CONTACT_ID, -1);
        this.contact = ContactProvider.getInstance().getContactByID(contactID);
        if (contact != null) {
            tvContactName.setText(contact.getName());
        } else {
            Log.e("ConvDisplay init", "failed to retrieve contact");
        }

        dvDrawDisplay.setLetterDisplay(this.tvLetterDisplay);
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

    private void initViews() {
        this.tvDrawPrompt = (TextView) this.findViewById(R.id.stat_tvDrawPrompt);
        this.dvDrawDisplay = (DrawingView) this.findViewById(R.id.dvDrawDisplay);
        this.ph_tvMessageDisplay = (TextView) this.findViewById(R.id.ph_tvMessageDisplay);
        this.tvLetterDisplay = (TextView) this.findViewById(R.id.tvLetterDisplay);
        this.btnSendMessage = (Button) this.findViewById(R.id.btnAcceptAllDrawBoard);
        this.btnBack = (Button) this.findViewById(R.id.btnBackDrawBoard);
        this.btnAccept = (Button) this.findViewById(R.id.btnAcceptLetterDrawBoard);
        this.layoutHistory = (RelativeLayout) this.findViewById(R.id.layoutHistory);
        this.layoutMessageButtons = (RelativeLayout) this.findViewById(R.id.layoutMessageButtons);
        this.layoutDrawBoard = (RelativeLayout) this.findViewById(R.id.conversationdisplay_drawboard);
        this.tvContactName = (TextView) this.findViewById(R.id.tvContactName);
    }

    private void setOnClicks() {
        layoutHistory.setOnTouchListener(new ActivitySwipeDetector(this, layoutHistory, layoutMessageButtons));
        btnAccept.setOnTouchListener(new ActivitySwipeDetector(this, btnAccept, layoutMessageButtons));
        btnSendMessage.setOnTouchListener(new ActivitySwipeDetector(this, btnSendMessage, layoutMessageButtons));
        btnBack.setOnTouchListener(new ActivitySwipeDetector(this, btnBack, layoutMessageButtons));

        // send message
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptAllGesture();
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

    @Override
    public void backGesture() {
        String text = dvDrawDisplay.backCommand();
        if (text != null) {
            displayToast("wiped");
            ph_tvMessageDisplay.setText(text);
            return;
        } else {
            displayToast("going back now");
        }
    }

    @Override
    public void acceptGesture() {
        ph_tvMessageDisplay.setText(dvDrawDisplay.acceptCommand());
    }

    @Override
    public void acceptAllGesture() {
        displayToast("sending message: " + ph_tvMessageDisplay.getText()); }

    private void displayToast(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }
}
