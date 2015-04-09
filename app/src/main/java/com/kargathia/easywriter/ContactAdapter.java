package com.kargathia.easywriter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    private Context context;
    private List<Contact> contacts;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.contacts = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = contacts.get(position);

        //TOOD: replace this simple view by the layout as defined in criminallistitem.xml"
        View view = convertView;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view  = inflater.inflate(R.layout.contacts_item, null);
        }
        TextView tv1 = (TextView) view.findViewById(R.id.tvContact);
        tv1.setText(contact.name);
        TextView tv2 = (TextView) view.findViewById(R.id.tvLastMessage);
            tv2.setText(contact.nummer);
        return view;
    }
}
