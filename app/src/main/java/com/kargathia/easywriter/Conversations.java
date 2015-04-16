package com.kargathia.easywriter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class Conversations extends Activity {

    ContactProvider provider = ContactProvider.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        provider.contacten = getContacts();

//Get a reference to the listview
        ListView listview = (ListView) findViewById(R.id.lvConversationDisplay);
        //Get a reference to the list with names

        //Create an adapter that feeds the data to the listview
        ContactAdapter adapter = new ContactAdapter(this, R.id.lvConversationDisplay, provider.contacten);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//Get the name from the array that is in the same position as the chosen listitem.
                //Todo start intent and pass name using putExtra
                Intent intent = new Intent(Conversations.this,ConversationDisplay.class);
                //doorsturen contactpersoon
                intent.putExtra("ContactPosition",position);
                startActivity(intent);
            }
        });
    }
    public List<Contact> getContacts(){
        List<Contact> contact = new ArrayList();
        //ophalen contacten
        Uri contant_uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contantResolver = getContentResolver();
        Cursor cursor = contantResolver.query(contant_uri, null, null, null, null);

        Uri phone_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


        //voor elke contact met telefoonnummer in de telefoon
        if(cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                String phone_number = "No number";

                String naam = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int number = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String phone_id =ContactsContract.CommonDataKinds.Phone.CONTACT_ID;

                System.out.println(contact_id);
                System.out.println(naam);

                //nummer ophalen per persoon
                if(number>0) {
                    Cursor phone_cursor = contantResolver.query(phone_uri, null, phone_id + " = ?", new String[]{contact_id}, null);
                    while (phone_cursor.moveToNext()) {
                        phone_number = phone_cursor.getString(phone_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phone_cursor.close();
                }
                //plaatje ophalen
                Drawable image = null;
                InputStream input = openPhoto(contant_uri, Long.parseLong(contact_id));
                if(input != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    image = new BitmapDrawable(bitmap);
                }

                contact.add(new Contact(this,naam, phone_number, null, image));
            }
        }
        cursor.close();

        //ophalen berichten


        
        return contact;
    }

    public InputStream openPhoto(Uri contact_uri, long contactId) {

        Uri contactUri = ContentUris.withAppendedId(contact_uri, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri,new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
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

    public void openConvHistory(){

    }

    public void newConv(){

    }
}
