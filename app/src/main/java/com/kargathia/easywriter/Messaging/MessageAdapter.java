package com.kargathia.easywriter.Messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kargathia.easywriter.Contacts.Contact;
import com.kargathia.easywriter.R;

import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private Contact contact;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message msg = super.getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.out_message_item, null);
        }

        return convertView;
    }
}
