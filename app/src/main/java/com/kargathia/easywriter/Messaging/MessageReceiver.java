package com.kargathia.easywriter.Messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.kargathia.easywriter.Contacts.ContactProvider;

/**
 * Created by Kargathia on 19/04/2015.
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";
            }
        }

        if (msgs != null) {
            //---display the new SMS message---
            Toast.makeText(context, messageReceived, Toast.LENGTH_SHORT).show();
            // Get the Sender Phone Number
            String senderPhoneNumber = msgs[0].getOriginatingAddress();

            Message msg = new Message(messageReceived, msgs[0].getTimestampMillis(), senderPhoneNumber, false);
            if (ContactProvider.getInstance().getContacten() == null) {
                ContactProvider.getInstance().retrieveContacts(context, null);
            }
            ContactProvider.getInstance().addMessage(msg);
        }

    }

    public static void fakeMessageReceived(Context context, String from) {
//        Log.i("receiver", "faking message from " + from);
        Message msg = new Message("fakeMessage", System.currentTimeMillis(), from, false);
        if (ContactProvider.getInstance().getContacten() == null) {
            ContactProvider.getInstance().retrieveContacts(context, null);
        }
        ContactProvider.getInstance().addMessage(msg);
    }
}
