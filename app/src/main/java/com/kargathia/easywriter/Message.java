package com.kargathia.easywriter;

import java.util.Date;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class Message {
    private String text;
    private Date datum;
    private String from;

    public String getText() {
        return this.text;
    }

    public Date getDate() {
        return this.datum;
    }

    public String getFrom() {
        return this.from;
    }


    public void setMessage(String tekst, Date datum, String from) {
        this.datum = datum;
        this.text = tekst;
        this.from = from;
    }
}
