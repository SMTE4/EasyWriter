package com.kargathia.easywriter.Conversations;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kargathia.easywriter.Contacts.Contact;
import com.kargathia.easywriter.Contacts.ContactAdapter;
import com.kargathia.easywriter.Contacts.ContactProvider;
import com.kargathia.easywriter.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Conversations extends Activity {

    ContactProvider provider = ContactProvider.getInstance();
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        provider.retrieveContacts(this, this);

        //Get a reference to the listview
        ListView listview = (ListView) findViewById(R.id.lvConversationDisplay);
        //Get a reference to the list with names

        //Displays message if no conversations found
        List<Contact> conversations = provider.getSmsContacten();
        TextView tvNoConvMessage = (TextView) findViewById(R.id.tvNoConversationMessage);
        if (conversations.isEmpty()) {
            tvNoConvMessage.setVisibility(View.VISIBLE);
        } else {
            tvNoConvMessage.setVisibility(View.GONE);
        }

        //Create an adapter that feeds the data to the listview
        ContactAdapter adapter = new ContactAdapter(this, R.id.lvConversationDisplay, conversations);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the name from the array that is in the same position as the chosen listitem.
                //Todo start intent and pass name using putExtra
                openConvHistory(position);
            }
        });

        Button newConversation = (Button) findViewById(R.id.btnNewConversation);
        newConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newConv();
            }
        });
    }

    public static Date millisToDate(long currentTime) {
        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        Date date = calendar.getTime();
        return date;
    }

    public InputStream openPhoto(Uri contact_uri, long contactId) {

        Uri contactUri = ContentUris.withAppendedId(contact_uri, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
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

    public void openConvHistory(int position) {
        Intent intent = new Intent(Conversations.this, ConversationDisplay.class);
        //doorsturen contactpersoon
        intent.putExtra(ConversationDisplay.INTENT_CONTACT_ID, provider.getSmsContacten().get(position).getID());
        startActivity(intent);
    }

    public void newConv() {
        Intent intent = new Intent(Conversations.this, NewConversation.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        provider.retrieveContacts(this, this);
    }
}
