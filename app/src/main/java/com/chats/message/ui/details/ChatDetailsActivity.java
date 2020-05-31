package com.chats.message.ui.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.chats.message.R;
import com.chats.message.model.Message;
import com.chats.message.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatDetailsActivity extends AppCompatActivity {

    public static final String KEY_CHATS = "com.chats.message.ui.details.KEY_CHATS";
    public static final String KEY_GROUP_ID = "com.chats.message.ui.details.KEY_GROUP_ID";
    private static final String TAG = "ChatDetailsActivity";

    private ListView mMessageListView;
    private ChatDetailsAdapter mAdapter;
    private EditText mTextBox;
    private Button mBtnSend;
    private User mUser;
    private DatabaseReference mDatabaseReference;
    private ChatDetailsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        mMessageListView = findViewById(R.id.messages_list_view);
        mTextBox = findViewById(R.id.edit_text_box);
        mBtnSend = findViewById(R.id.btn_send);

        mAdapter = new ChatDetailsAdapter(this, R.layout.item_message, new ArrayList<Message>());
        mMessageListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_CHATS)){
            mUser = intent.getParcelableExtra(KEY_CHATS);
            setMessagesDbReference();
        } else if (intent.hasExtra(KEY_GROUP_ID)){
            String groupId = intent.getStringExtra(KEY_GROUP_ID);
            setGroupMessagesDbReference(groupId);
        }
        mViewModel = new ViewModelProvider(this).get(ChatDetailsViewModel.class);
        mViewModel.getMessages().observe(this, new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                mAdapter.add(message);
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setGroupMessagesDbReference(String groupId) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("group_messages")
                .child(groupId);
    }

    private void setMessagesDbReference() {
        String uid1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String uid2 = mUser.getUserId();
        String chatsId = oneToOneChatId(uid1, uid2);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.db_node_messages))
                .child(chatsId);
    }

    private void sendMessage() {
        Message message = new Message();
        if (mUser != null) { // one to one message(user to user)
            String messageContent = mTextBox.getText().toString().trim();
            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String receiverId = mUser.getUserId();
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setTextMessage(messageContent);
        } else { // group message
            message.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            message.setTextMessage(mTextBox.getText().toString().trim());
        }
        mDatabaseReference.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: message sent successfully");
                } else {
                    Log.d(TAG, "onComplete: could not send message");
                }
            }
        });
    }

    private String oneToOneChatId(String uid1, String uid2){
        if (uid1.compareTo(uid2) < 0){
            return uid1 + uid2;
        } else {
            return uid2 + uid1;
        }
    }

    private ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Message message = dataSnapshot.getValue(Message.class);
            mViewModel.setMessages(message);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addChildEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (eventListener != null){
            mDatabaseReference.removeEventListener(eventListener);
        }
    }
}
