package com.kargathia.easywriter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
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
    }
}
