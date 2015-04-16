package com.kargathia.easywriter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class Contact {
    public String name;
    public List<Message> messages;
    public int lastMessage = 0;
    public  String nummer = "No number";
    public Drawable image;
    private Context context;

    public Contact(Context contaxt, String naam,String nummer, List<Message> messageList, Drawable image)
    {
        this.context = contaxt;
        this.name = naam;
        if(messageList == null)
        {
            this.messages = new ArrayList();
        }
        else {
            this.messages = messageList;
            this.lastMessage = messageList.size();
        }
        if(image == null)
        {
            this.image =  context.getResources().getDrawable(R.drawable.contact_icon);
        }
        else
        {
            this.image = image;
        }
        this.nummer = nummer;
    }
    public void addMessage(Message message)
    {
        messages.add(message);
    }

}
