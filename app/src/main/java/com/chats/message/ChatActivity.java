package com.chats.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chats.message.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = ChatActivity.class.getName();

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private MessageAdapter messageAdapter;
    private ImageButton btnSend;

    // widgets
    private EditText etTextMessage;
    private LinearLayout rootView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etTextMessage = findViewById(R.id.text_message);
        btnSend = findViewById(R.id.message_send);
        rootView = findViewById(R.id.root_view);
        listView = findViewById(R.id.list_view);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages");

        List<Message> messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.item_message, messages);
        listView.setAdapter(messageAdapter);

        setUpFirebaseAuth();

        // Enable Send button when there's text to send
        etTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0){
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setUpChildEventListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageAdapter.add(message);
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

        databaseReference.addChildEventListener(childEventListener);
    }

    private void detachChildEventListener(){
        if (childEventListener != null){
            databaseReference.removeEventListener(childEventListener);
        }
    }

    private void setUpFirebaseAuth() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: user authenticated");
                } else {
                    Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthState();
    }

    private void checkAuthState() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "checkAuthState: user is authenticated");
            setUpChildEventListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachChildEventListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    public void sendMessage(View view) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String sender = email.substring(0, email.indexOf("@"));
        String textMessage = etTextMessage.getText().toString();

        Message message = new Message(sender, textMessage, null);
        databaseReference.push().setValue(message);

        etTextMessage.setText("");
    }
}
