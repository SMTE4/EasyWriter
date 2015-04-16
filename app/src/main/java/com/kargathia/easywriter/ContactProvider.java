package com.kargathia.easywriter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linda on 9-4-2015.
 */
public class ContactProvider {
    private static ContactProvider instance = null;
    private List<Contact> contacten;
    private List<Contact> smsContacten;

    public List<Contact> getContacten(){
        return contacten;
    }
    public List<Contact> getSmsContacten()
    {
        return this.smsContacten;
    }

    public static ContactProvider getInstance() {
        if(instance == null) {
            instance = new ContactProvider();
        }
        return instance;
    }
    private ContactProvider()
    {
        this.contacten = new ArrayList<Contact>();
        this.smsContacten = new ArrayList<Contact>();
    }

    public void setContacten(List<Contact> list)
    {
        this.contacten = list;
    }
    public  void setSmsContacten(List<Contact> list)
    {
        this.smsContacten = list;
    }
}
