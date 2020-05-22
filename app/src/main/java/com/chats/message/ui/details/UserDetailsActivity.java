package com.chats.message.ui.details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chats.message.R;
import com.chats.message.model.User;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String KEY_INCOMING_USER = "com.chats.message.ui.details.KEY_INCOMING_USER";

    // widgets
    private TextView userName, userEmail;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        profilePicture = findViewById(R.id.profile_picture);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_INCOMING_USER)){
            User user = intent.getParcelableExtra(KEY_INCOMING_USER);
            if (user != null) {
                setUserDetails(user);
            }
        }
    }

    private void setUserDetails(User user) {
        userName.setText(user.getUserName());
        userEmail.setText(user.getEmail());
        Glide.with(this)
                .load(user.getProfilePicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(profilePicture);
    }
}
