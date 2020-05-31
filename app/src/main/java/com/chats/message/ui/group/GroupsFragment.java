package com.chats.message.ui.group;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chats.message.R;
import com.chats.message.model.Group;
import com.chats.message.ui.details.ChatDetailsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment implements
        GroupAdapter.OnGroupItemClickListener {

    private GroupAdapter mAdapter;
    private DatabaseReference mDbReference;
    private GroupViewModel mViewHolder;
    private List<Group> mGroups;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.db_node_groups));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.groups_recycler_view);
        mAdapter = new GroupAdapter(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewHolder = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        mViewHolder.getGroups().observe(this, new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                mGroups = groups;
                mAdapter.setGroups(groups);
            }
        });
    }

    private ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Group> groups = new ArrayList<>();
            for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                Group group = singleSnapshot.getValue(Group.class);
                groups.add(group);
            }
            mViewHolder.setGroups(groups);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    @Override
    public void onStart() {
        super.onStart();
        mDbReference.addValueEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDbReference.removeEventListener(eventListener);
    }

    @Override
    public void onItemClick(int position) {
        Group group = mGroups.get(position);
        Intent intent = new Intent(getActivity(), ChatDetailsActivity.class);
        intent.putExtra(ChatDetailsActivity.KEY_GROUP_ID, group.getGroupId());
        startActivity(intent);
    }
}
