package com.chats.message.ui.users;

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
import com.chats.message.model.User;
import com.chats.message.ui.MainActivity;
import com.chats.message.ui.details.UserDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment implements UserAdapter.OnListItemClickListener {

    private static final String TAG = "UsersFragment";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private UserViewModel mViewModel;
    private UserAdapter mAdapter;
    private List<User> mUsers;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        RecyclerView userRecyclerView = view.findViewById(R.id.user_recycler_view);
        mAdapter = new UserAdapter(getActivity());
        mAdapter.setOnListItemClickListener(this);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        mViewModel.getUsers().observe(requireActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter.setUsers(users);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // attach listener
        mDatabaseReference.addValueEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove listener
        if (eventListener != null) {
            mDatabaseReference.removeEventListener(eventListener);
        }
    }

    private ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mUsers = new ArrayList<>();
            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                User user = singleSnapshot.getValue(User.class);
                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getUserId())) {
                    mUsers.add(user);
                }
            }
            mViewModel.setUsers(mUsers);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), UserDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.KEY_INCOMING_USER, mUsers.get(position));
        startActivity(intent);
    }
}
