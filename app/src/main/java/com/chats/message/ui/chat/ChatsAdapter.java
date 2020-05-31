package com.chats.message.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chats.message.R;
import com.chats.message.model.ChatItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    private List<ChatItem> mChatItems;
    private Context mContext;
    private OnChatItemClickListener mOnChatItemClickListener;

    interface OnChatItemClickListener{
        void onItemClick(int position);
    }

    public void setOnChatItemClickListener(OnChatItemClickListener itemClickListener) {
        this.mOnChatItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(view, mOnChatItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chatItem = mChatItems.get(position);
        holder.userName.setText(chatItem.getUser().getUserName());
        holder.messageSnippet.setText(chatItem.getMessageSnippet());
        if (chatItem.getUser().getProfilePicture() != null){
            Glide.with(mContext)
                    .load(chatItem.getUser().getProfilePicture())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.profilePicture);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_person)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.profilePicture);
        }
    }

    @Override
    public int getItemCount() {
        return mChatItems.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView userName, messageSnippet, timestamp;
        private CircleImageView profilePicture;
        private OnChatItemClickListener itemClickListener;

        public ChatViewHolder(@NonNull View itemView, OnChatItemClickListener itemClickListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            messageSnippet = itemView.findViewById(R.id.message_snippet);
            timestamp = itemView.findViewById(R.id.time);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            this.itemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
