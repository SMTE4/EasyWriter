package com.kargathia.easywriter.Contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.kargathia.easywriter.Messaging.Message;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Linda on 9-4-2015.
 */
public class ContactProvider {
    private static ContactProvider instance = null;
    private List<Contact>
            contacten,
            smsContacten;

    public List<Contact> getContacten() {
        return contacten;
    }

    public List<Contact> getSmsContacten() {
        return smsContacten;
    }


    public static ContactProvider getInstance() {
        if (instance == null) {
            instance = new ContactProvider();
        }
        return instance;
    }

    private ContactProvider() {
        this.contacten = null;
        this.smsContacten = null;
    }

    public void setContacten(List<Contact> list) {
        this.contacten = list;
    }

    public void setSmsContacten(List<Contact> list) {
        this.smsContacten = list;
    }

    public List<Contact> retrieveContacts(Context context, Activity manager) {
        int id = 0;
        List<Contact> contact = new ArrayList<>();
        List<Contact> smsContacten = new ArrayList<>();
        //ophalen contacten
        Uri contant_uri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contantResolver = context.getContentResolver();
        Cursor cursor = contantResolver.query(contant_uri, null, null, null, null);
        manager.startManagingCursor(cursor);

        Uri phone_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        Log.i("retrieving contacts", String.valueOf(cursor.getCount()));
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
                InputStream input = openPhoto(contant_uri, Long.parseLong(contact_id), context);
                if (input != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    image = new BitmapDrawable(bitmap);
                }
                Contact tijdelijk = new Contact(id, context, naam, phone_number, image);
                if (!phone_number.equals("No number")) {
                    boolean bestaat = false;
                    for (Contact x : contact) {
                        if (x.getNummer().equals(tijdelijk.getNummer())) {
                            bestaat = true;
                        }
                    }
                    if (!bestaat) {
                        contact.add(tijdelijk);
                        id++;
                    }
                }
            }
        }
        cursor.close();

        //ophalen berichten
        List<Message> smsList = new ArrayList<>();

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        manager.startManagingCursor(c);

        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            while (c.moveToNext()) {
                Message sms = new Message();
                String text = c.getString(c.getColumnIndexOrThrow("body"));
                String date = c.getString(c.getColumnIndex("date"));
                String adres = c.getString(c.getColumnIndexOrThrow("address"));
                sms.setMessage(text, millisToDate(Long.parseLong(date)), adres, false);
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
        for (Contact x : contact) {
            if (x.getMessages().size() > 0) {
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
                x.setLastMessage(x.getMessages().get(x.getMessages().size() - 1).getDate());
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
        this.setSmsContacten(smsContacten);
        this.setContacten(contact);
        return contact;
    }

    private Date millisToDate(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        return calendar.getTime();
    }

    private InputStream openPhoto(Uri contact_uri, long contactId, Context context) {

        Uri contactUri = ContentUris.withAppendedId(contact_uri, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver()
                .query(photoUri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
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
}
