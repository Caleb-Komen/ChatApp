package com.chats.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chats.message.model.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        TextView tvMessage = convertView.findViewById(R.id.text_message);
        TextView tvSender = convertView.findViewById(R.id.user_email);
        ImageView photoImageView = convertView.findViewById(R.id.profile_picture);

        Message message = getItem(position);
        String textMessage = message.getTextMessage();
        String sender = message.getSender();
        String photoUrl = message.getPhotoUrl();

        if (photoUrl == null){
            photoImageView.setVisibility(View.GONE);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(textMessage);

        } else {
            tvMessage.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(photoUrl)
                    .into(photoImageView);
        }

        tvSender.setText(sender);
        return convertView;
    }
}
