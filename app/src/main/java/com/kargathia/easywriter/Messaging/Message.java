package com.kargathia.easywriter.Messaging;

import java.util.Date;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class Message {
    private String text;
    private Date datum;
    private String from;
    private boolean isOutGoing;

    public String getText() {
        return this.text;
    }

    public Date getDate() {
        return this.datum;
    }

    public String getFrom() {
        return this.from;
    }

    public boolean isOutGoing(){ return this.isOutGoing; }


    public void setMessage(String tekst, Date datum, String from, boolean isOutGoing) {
        this.datum = datum;
        this.text = tekst;
        this.from = from;
        this.isOutGoing = isOutGoing;
    }
}
