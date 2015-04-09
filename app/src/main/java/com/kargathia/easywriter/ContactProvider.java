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
    public List<Contact> contacten;
    protected ContactProvider() {
        // Exists only to defeat instantiation.
        contacten = new ArrayList();
    }

    public static ContactProvider getInstance() {
        if(instance == null) {
            instance = new ContactProvider();
        }
        return instance;
    }
}
