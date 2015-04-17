package com.kargathia.easywriter.Contacts;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.kargathia.easywriter.Messaging.Message;
import com.kargathia.easywriter.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class Contact {
    private String name;
    private List<Message> messages;
    private Date lastMessage = null;
    private String nummer = "No number";
    private Drawable image;
    private Context context;
    private int id = 0;

    public Contact(int id, Context contaxt, String naam, String nummer, Drawable image) {
        this.context = contaxt;
        this.id = id;
        this.name = naam;

        this.messages = new ArrayList();

        if (image == null) {
            this.image = context.getResources().getDrawable(R.drawable.contact_icon);
        } else {
            this.image = image;
        }
        this.nummer = nummer;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public String getName() {
        return this.name;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public String getNummer() {
        return this.nummer;
    }

    public Drawable getImage() {
        return this.image;
    }

    public int getID() {
        return this.id;
    }

    public Date getLastMessage() {
        return this.lastMessage;
    }

    public void setSortedMessages(List<Message> list) {
        this.messages = list;
    }

    public void setLastMessage(Date date) {
        this.lastMessage = date;
    }
}
