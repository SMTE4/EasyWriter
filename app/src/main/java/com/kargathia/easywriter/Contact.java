package com.kargathia.easywriter;

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

    public Contact(String naam,String nummer, List<Message> messageList)
    {
        this.name = naam;
        if(messageList == null)
        {
            this.messages = new ArrayList();
        }
        else {
            this.messages = messageList;
            this.lastMessage = messageList.size();
        }

            this.nummer = nummer;
    }
    public void addMessage(String message, Date datum)
    {
//        messages.add(new Message(message, datum));
    }

}
