package com.chats.message.ui.users;

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
import com.chats.message.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> mUsers = new ArrayList<>();

    public UserAdapter(Context context) {
        this.context = context;
    }

    public void setUsers(List<User> users){
        mUsers = users;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUserName());
        Glide.with(context)
                .load(user.getProfilePicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private CircleImageView profilePicture;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            profilePicture = itemView.findViewById(R.id.profile_picture);
        }
    }
}
