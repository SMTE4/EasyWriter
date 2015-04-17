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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


public class Conversations extends Activity {

    ContactProvider provider = ContactProvider.getInstance();
    private int id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        provider.setContacten(getContacts());

        //Get a reference to the listview
        ListView listview = (ListView) findViewById(R.id.lvConversationDisplay);
        //Get a reference to the list with names

        //Create an adapter that feeds the data to the listview
        ContactAdapter adapter = new ContactAdapter(this, R.id.lvConversationDisplay, provider.getSmsContacten());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
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
    public List<Contact> getContacts() {
        List<Contact> contact = new ArrayList<Contact>();
        List<Contact> smsContacten = new ArrayList<Contact>();
        //ophalen contacten
        Uri contant_uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contantResolver = getContentResolver();
        Cursor cursor = contantResolver.query(contant_uri, null, null, null, null);

        Uri phone_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


        //voor elke contact met telefoonnummer in de telefoon
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String phone_number = "No number";

                String naam = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int number = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String phone_id = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;


                //nummer ophalen per persoon
                if (number > 0) {
                    Cursor phone_cursor = contantResolver.query(phone_uri, null, phone_id + " = ?", new String[]{contact_id}, null);
                    while (phone_cursor.moveToNext()) {
                        phone_number = phone_cursor.getString(phone_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phone_cursor.close();
                }
                //plaatje ophalen
                Drawable image = null;
                InputStream input = openPhoto(contant_uri, Long.parseLong(contact_id));
                if (input != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    image = new BitmapDrawable(bitmap);
                }
                Contact tijdelijk = new Contact(id,this, naam, phone_number, image);
                if(!phone_number.equals("No number")) {
                    boolean bestaat =false;
                    for(Contact x : contact)
                    {
                        if(x.getNummer().equals(tijdelijk.getNummer()))
                        {
                            bestaat = true;
                        }
                    }
                    if(bestaat == false) {
                        contact.add(tijdelijk);
                        id++;
                    }
                }
            }
        }
        cursor.close();

        //ophalen berichten
        List<Message> smsList = new ArrayList<Message>();

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);

        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            while(c.moveToNext()) {
                Message sms = new Message();
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String date = c.getString(c.getColumnIndex("date")).toString();
                String adres = c.getString(c.getColumnIndexOrThrow("address")).toString();
                sms.setMessage(text, millisToDate(Long.parseLong(date)), adres);
                smsList.add(sms);
            }
        }

        for (Message x : smsList) {
            for (Contact a : contact) {
                if (x.getFrom().equals(a.getNummer())) {
                    a.addMessage(x);
                }
            }
        }
        c.close();

        //contacten sorteren
        Collections.sort(contact, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                String name1 = c1.getName().toLowerCase();
                String name2 = c2.getName().toLowerCase();
                return name1.compareTo(name2);
            }
        });

        //berichten sorterren
        for(Contact x : contact) {
            if (x.getMessages().size()>0) {
                List<Message> messages = x.getMessages();
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message m1, Message m2) {
                        Date date1 = m1.getDate();
                        Date date2 = m2.getDate();
                        return date1.compareTo(date2);
                    }
                });
                x.setSortedMessages(messages);
                smsContacten.add(x);
                x.setLastMessage(x.getMessages().get(x.getMessages().size()-1).getDate());
            }
        }

        //smsconcaten sorteren op laatste bericht

        Collections.sort(smsContacten, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                Date name1 = c1.getLastMessage();
                Date name2 = c2.getLastMessage();
                return name2.compareTo(name1);
            }
        });
        provider.setSmsContacten(smsContacten);

        return contact;
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

    public void openConvHistory(int position){
        Intent intent = new Intent(Conversations.this,ConversationDisplay.class);
        //doorsturen contactpersoon
        intent.putExtra("ContactPosition",provider.getSmsContacten().get(position).getID());
        startActivity(intent);
    }

    public void newConv(){
        Intent intent = new Intent(Conversations.this, NewConversation.class);
        startActivity(intent);
    }
    @Override
    public void onResume(){
        super.onResume();
        provider.setContacten(getContacts());
    }
}
