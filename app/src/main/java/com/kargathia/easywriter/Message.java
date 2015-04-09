package com.kargathia.easywriter;

import java.util.Date;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class Message {
    public String text;
    public Date datum;

    public Message(String tekst, Date datum){
        this.datum = datum;
        this.text = tekst;
    }
}
