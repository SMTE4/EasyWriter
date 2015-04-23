package com.kargathia.easywriter.Conversations;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kargathia.easywriter.Contacts.Contact;
import com.kargathia.easywriter.Contacts.ContactAdapter;
import com.kargathia.easywriter.Contacts.ContactProvider;
import com.kargathia.easywriter.Drawing.ActivitySwipeDetector;
import com.kargathia.easywriter.Drawing.DrawingView;
import com.kargathia.easywriter.Drawing.IActivitySwipeInterpreter;
import com.kargathia.easywriter.R;

import java.util.List;


public class NewConversation extends Activity implements IActivitySwipeInterpreter {

    private ContactProvider provider;
    private Context context;

    private RelativeLayout
            layoutContacts,
            layoutButtonsBoard,
            layoutDrawBoard;
    private Button
            btnBackBoard,
            btnAcceptLetterBoard,
            btnAcceptAllBoard;
    private ListView lvContacts;
    private DrawingView dvDrawDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        this.provider = ContactProvider.getInstance();
        this.context = this;
        this.initViews();
        this.setOnClicks();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_conversation, menu);
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

    /**
     * Does all the findViewById operations
     */
    private void initViews() {
        layoutContacts = (RelativeLayout) findViewById(R.id.layoutContacts);
        layoutDrawBoard = (RelativeLayout) findViewById(R.id.newconversation_drawboard);
        layoutButtonsBoard = (RelativeLayout) findViewById(R.id.layoutMessageButtons);
        btnAcceptLetterBoard = (Button) findViewById(R.id.btnAcceptLetterDrawBoard);
        btnAcceptAllBoard = (Button) findViewById(R.id.btnAcceptAllDrawBoard);
        btnBackBoard = (Button) findViewById(R.id.btnBackDrawBoard);
        dvDrawDisplay = (DrawingView) findViewById(R.id.dvDrawDisplay);
        lvContacts = (ListView) findViewById(R.id.lvContacts);
    }

    /**
     * Sets all listeners
     */
    private void setOnClicks() {
        layoutContacts.setOnTouchListener(new ActivitySwipeDetector(this, layoutContacts, layoutButtonsBoard));
        btnAcceptLetterBoard.setOnTouchListener(new ActivitySwipeDetector(this, btnAcceptLetterBoard, layoutButtonsBoard));
        btnAcceptAllBoard.setOnTouchListener(new ActivitySwipeDetector(this, btnAcceptAllBoard, layoutButtonsBoard));
        btnBackBoard.setOnTouchListener(new ActivitySwipeDetector(this, btnBackBoard, layoutButtonsBoard));

        // send message
        btnAcceptAllBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptAllGesture();
            }
        });

        btnAcceptLetterBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptGesture();
            }
        });

        btnBackBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backGesture();
            }
        });

        //Get a reference to the list with names
        List<Contact> contacts = provider.getContacten();
        if (contacts == null) {
            contacts = provider.retrieveContacts(this, this);
        }

        //Create an adapter that feeds the data to the listview
        ContactAdapter adapter = new ContactAdapter(this, R.id.lvConversationDisplay, contacts);
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int ID = provider.getContacten().get(position).getID();
                ConversationDisplay.startNew(context, ID);
            }
        });
    }


    public void searchContact() {

    }

    @Override
    public void backGesture() {

    }

    @Override
    public void acceptGesture() {

    }

    @Override
    public void acceptAllGesture() {
        //                displayToast("searching for " + ph_tvMessageDisplay.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("resuming newConv", "retrieving contacts");
        provider.retrieveContacts(this, this);
    }

    private void displayToast(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(this, text, duration).show();
    }
}
