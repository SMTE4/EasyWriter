package com.kargathia.easywriter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class Contact{
    private String name;
    private List<Message> messages;
    private int lastMessage = 0;
    private  String nummer = "No number";
    private Drawable image;
    private Context context;

    public Contact(Context contaxt, String naam,String nummer, Drawable image) {
        this.context = contaxt;
        this.name = naam;

        this.messages = new ArrayList();

        if (image == null) {
            this.image = context.getResources().getDrawable(R.drawable.contact_icon);
        } else {
            this.image = image;
        }
        this.nummer = nummer;
    }
    public void addMessage(Message message)
    {
        messages.add(message);
        lastMessage = messages.size();
    }

    public String getName(){
        return this.name;
    }
    public List<Message> getMessages(){
        return this.messages;
    }
    public int getLastMessage(){
        return this.lastMessage;
    }
    public String getNummer(){
        return this.nummer;
    }
    public Drawable getImage(){
        return this.image;
    }
    public void setSortedMessages(List<Message> list)
    {
        this.messages = list;
    }
}
