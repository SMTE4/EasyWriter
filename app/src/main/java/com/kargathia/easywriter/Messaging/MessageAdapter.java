package com.kargathia.easywriter.Messaging;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kargathia.easywriter.R;

import java.util.List;

/**
 * Created by Kargathia on 02/04/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
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
            convertView = inflater.inflate(R.layout.message_item, null);
        }
        TextView tvMessageDisplay = (TextView) convertView.findViewById(R.id.tv_msg_text);
        TextView tvDateDisplay = (TextView) convertView.findViewById(R.id.tv_msg_date);
        LinearLayout statLayoutMessageItem = (LinearLayout) convertView.findViewById(R.id.stat_layoutmessageitem);
        LinearLayout layoutMessageItem = (LinearLayout) convertView.findViewById(R.id.layoutMessageItem);


        tvMessageDisplay.setText(msg.getText());
        tvDateDisplay.setText(msg.getDate().toString()); // TODO fix formatting

        if(msg.isOutGoing()){
            statLayoutMessageItem.setGravity(Gravity.RIGHT);
            layoutMessageItem.setBackgroundColor(context.getResources().getColor(R.color.msg_out));
        } else {
            statLayoutMessageItem.setGravity(Gravity.LEFT);
            layoutMessageItem.setBackgroundColor(context.getResources().getColor(R.color.msg_in));
        }

        return convertView;
    }
}
