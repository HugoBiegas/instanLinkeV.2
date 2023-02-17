package com.example.instantlike.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.instantlike.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private int mResource;
    private String idOther;
    private String idYou;
    public MessageAdapter(Context context, int resource, List<Message> messages,String idOther,String idYou) {
        super(context, resource, messages);
        mContext = context;
        mResource = resource;
        this.idOther = idOther;
        this.idYou = idYou;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }
        TextView messageTextView,senderTextView;

        Message message = getItem(position);

        if (message.getSender().equals(idOther) ){
             messageTextView = convertView.findViewById(R.id.messageTextViewGauche);
             senderTextView = convertView.findViewById(R.id.senderTextViewGauche);

        }else {
             messageTextView = convertView.findViewById(R.id.messageTextViewDroit);
             senderTextView = convertView.findViewById(R.id.senderTextViewDroit);
        }
        messageTextView.setText(message.getText());
        senderTextView.setText(message.getDate());

        return convertView;
    }
}
