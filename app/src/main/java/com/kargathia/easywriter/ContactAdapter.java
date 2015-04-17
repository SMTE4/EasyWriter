package com.kargathia.easywriter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    private Context context;
    private List<Contact> contacts;
    private Locale currentLocale = Locale.US;

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

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contacts_item, null);
        }
        ImageView iv1 = (ImageView) view.findViewById(R.id.ivContact);
        iv1.setImageDrawable(contact.getImage());
        TextView tv1 = (TextView) view.findViewById(R.id.tvContact);
        tv1.setText(contact.getName());

        TextView tv2 = (TextView) view.findViewById(R.id.tvLastMessage);
        if (contact.getLastMessage() == null) {
            tv2.setText("There are no messages");
        } else {
            String date = DateFormat.getDateInstance(DateFormat.LONG, currentLocale).format(contact.getMessages().get(contact.getMessages().size() - 1).getDate());
            String last = "; ";
            String text = contact.getMessages().get(contact.getMessages().size() - 1).getText();
            String end = text;
            if (text.length() > 30) {
                end = end.substring(0, 30);
            }
            String dot = "...";
            tv2.setText(date + last + end + dot);
        }


        return view;
    }
}
