package com.kargathia.easywriter.Conversations;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kargathia.easywriter.Contacts.Contact;
import com.kargathia.easywriter.Contacts.ContactProvider;
import com.kargathia.easywriter.Drawing.ActivitySwipeDetector;
import com.kargathia.easywriter.Drawing.DrawingView;
import com.kargathia.easywriter.Drawing.IActivitySwipeInterpreter;
import com.kargathia.easywriter.Messaging.Message;
import com.kargathia.easywriter.Messaging.MessageAdapter;
import com.kargathia.easywriter.R;


public class ConversationDisplay extends Activity implements IActivitySwipeInterpreter {

    public static String
            INTENT_CONTACT_ID = "com.kargathia.easywriter.contactID";

    private RelativeLayout
            layoutMessageButtons,
            layoutHistory,
            layoutDrawBoard;
    private ListView lvMessageHistory;
    private TextView
            tvDrawPrompt,
            tvLetterDisplay,
            tvContactName;
    private EditText etNewMessage;
    private DrawingView dvDrawDisplay;
    private Button
            btnSendMessage,
            btnBack,
            btnAccept;
    private Contact contact = null;
    private MessageAdapter adapter;
    private SmsManager smsManager;

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
        this.smsManager = SmsManager.getDefault();

        this.initViews();

        Intent intent = getIntent();
        int contactID = intent.getIntExtra(INTENT_CONTACT_ID, -1);
        ContactProvider provider = ContactProvider.getInstance();
        this.contact = provider.getContactByID(contactID);
        if (contact != null) {
            tvContactName.setText(contact.getName());
//            addPlaceHolderMessages(contact);
            provider.subScribeToContact(contactID, this);
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

//    private void addPlaceHolderMessages(Contact input) {
//        if (!input.getMessages().isEmpty()) {
//            return;
//        }
//
//        for (int rep = 1; rep < 20; rep++) {
//            boolean outgoing = (rep % 3 == 1);
//            String text = "message text " + rep;
//
//            Message msg = new Message(text, System.currentTimeMillis(), "", outgoing);
//            input.addMessage(msg);
//        }
//    }

    public void notifyDataChanged(Message msg) {
        adapter.notifyDataSetChanged();
        lvMessageHistory.setSelection(adapter.getCount() - 1);
    }

    private void initViews() {
        this.tvDrawPrompt = (TextView) this.findViewById(R.id.stat_tvDrawPrompt);
        this.dvDrawDisplay = (DrawingView) this.findViewById(R.id.dvDrawDisplay);
        this.tvLetterDisplay = (TextView) this.findViewById(R.id.tvLetterDisplay);
        this.btnSendMessage = (Button) this.findViewById(R.id.btnAcceptAllDrawBoard);
        this.btnBack = (Button) this.findViewById(R.id.btnBackDrawBoard);
        this.btnAccept = (Button) this.findViewById(R.id.btnAcceptLetterDrawBoard);
        this.layoutHistory = (RelativeLayout) this.findViewById(R.id.layoutHistory);
        this.layoutMessageButtons = (RelativeLayout) this.findViewById(R.id.layoutMessageButtons);
        this.layoutDrawBoard = (RelativeLayout) this.findViewById(R.id.conversationdisplay_drawboard);
        this.tvContactName = (TextView) this.findViewById(R.id.tvContactName);
        this.lvMessageHistory = (ListView) this.findViewById(R.id.lvHistory);
        this.etNewMessage = (EditText) this.findViewById(R.id.etNewMessage);
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

        //Create an adapter that feeds the data to the listview
        this.adapter = new MessageAdapter(this, R.id.lvHistory, contact.getMessages());
        lvMessageHistory.setAdapter(adapter);
        lvMessageHistory.setItemsCanFocus(false);
        lvMessageHistory.setDivider(null);

        etNewMessage.setKeyListener(null);
        etNewMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.equals(etNewMessage) && hasFocus) {
                    startMessage();
                }
                lvMessageHistory.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void startMessage() {
        Log.i("onClickConv", "startMessage");
        this.layoutDrawBoard.setVisibility(View.VISIBLE);
        etNewMessage.setCursorVisible(true);
    }

    private void endMessage() {
        etNewMessage.setText("");
        etNewMessage.clearFocus();
        etNewMessage.setCursorVisible(false);
        dvDrawDisplay.clearCommand();
        layoutDrawBoard.setVisibility(View.GONE);
        lvMessageHistory.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void backGesture() {
        if (dvDrawDisplay.clearCommand()) {
            displayToast("wiped");
        } else {
            String prevText = etNewMessage.getText().toString();
            if (prevText.length() > 0) {
                etNewMessage.setText(prevText.substring(0, prevText.length() - 1));
            }
        }
    }

    @Override
    public void acceptGesture() {
        String prevText = etNewMessage.getText().toString();
        etNewMessage.setText(prevText.concat(dvDrawDisplay.acceptCommand()));
    }

    @Override
    public void acceptAllGesture() {
        String smsText = etNewMessage.getText().toString();
        acceptGesture();
        displayToast("sending message: " + smsText);
//        MessageReceiver.fakeMessageReceived(this, contact.getNummer(), smsText);
        this.endMessage();

        // Turn this on when really testing
        smsManager.sendTextMessage(contact.getNummer(), null, smsText, null, null);
        ContactProvider.getInstance().addMessage(this,
                new Message(smsText, System.currentTimeMillis(), contact.getNummer(), true));
        adapter.notifyDataSetChanged();
    }

    private void displayToast(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }
}
