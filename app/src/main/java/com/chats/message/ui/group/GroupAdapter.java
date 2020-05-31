package com.chats.message.ui.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chats.message.R;
import com.chats.message.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> mGroups = new ArrayList<>();
    private Context mContext;
    private OnGroupItemClickListener mItemClickListener;

    interface OnGroupItemClickListener{
        void onItemClick(int position);
    }

    public void setItemClickListener(OnGroupItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public GroupAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setGroups(List<Group> groups) {
        mGroups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = mGroups.get(position);
        holder.groupName.setText(group.getGroupName());
        Glide.with(mContext)
                .load(group.getGroupProfile())
                .placeholder(R.drawable.ic_person)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.groupProfile);
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView groupName, noOfMembers;
        private ImageView groupProfile;
        private OnGroupItemClickListener itemClickListener;

        public GroupViewHolder(@NonNull View itemView, OnGroupItemClickListener itemClickListener){
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            noOfMembers = itemView.findViewById(R.id.no_of_members);
            groupProfile = itemView.findViewById(R.id.group_profile);
            this.itemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
