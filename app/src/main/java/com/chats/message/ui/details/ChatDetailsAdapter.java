package com.chats.message.ui.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chats.message.R;
import com.chats.message.model.Message;

import java.util.List;

public class ChatDetailsAdapter extends ArrayAdapter<Message> {
    private Context mContext;

    public ChatDetailsAdapter(@NonNull Context context,int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
        Message message = getItem(position);
        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        tvMessage.setText(message.getTextMessage());
        return convertView;
    }
}
