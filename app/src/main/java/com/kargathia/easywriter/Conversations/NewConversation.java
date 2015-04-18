package com.kargathia.easywriter.Conversations;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.kargathia.easywriter.Contacts.ContactAdapter;
import com.kargathia.easywriter.Contacts.ContactProvider;
import com.kargathia.easywriter.Drawing.IActivitySwipeInterpreter;
import com.kargathia.easywriter.R;


public class NewConversation extends Activity implements IActivitySwipeInterpreter {

    private ContactProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        provider = ContactProvider.getInstance();

        //Get a reference to the listview
        ListView lvContacts = (ListView) findViewById(R.id.lvContacts);
        //Get a reference to the list with names

        //Create an adapter that feeds the data to the listview
        ContactAdapter adapter = new ContactAdapter(this, R.id.lvConversationDisplay, provider.getSmsContacten());
        lvContacts.setAdapter(adapter);
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

    public void searchContact() {

    }

    public void startConversation() {

    }

    @Override
    public void backGesture() {

    }

    @Override
    public void acceptGesture() {

    }
}
