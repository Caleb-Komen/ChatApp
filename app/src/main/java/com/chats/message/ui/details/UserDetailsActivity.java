package com.chats.message.ui.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chats.message.ChatActivity;
import com.chats.message.R;
import com.chats.message.model.User;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String KEY_INCOMING_USER = "com.chats.message.ui.details.KEY_INCOMING_USER";

    // widgets
    private TextView mUserName, mUserEmail;
    private ImageView mProfilePicture;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        initToolbar();
        UserDetailsViewModel viewModel = new ViewModelProvider(this).get(UserDetailsViewModel.class);
        mUserName = findViewById(R.id.user_name);
        mUserEmail = findViewById(R.id.user_email);
        mProfilePicture = findViewById(R.id.profile_picture);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_INCOMING_USER)){
            mUser = intent.getParcelableExtra(KEY_INCOMING_USER);
            if (mUser != null) {
                viewModel.setUser(mUser);
            }
        }

        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUserDetails(user);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUserDetails(User user) {
        mUserName.setText(user.getUserName());
        mUserEmail.setText(user.getEmail());
        Glide.with(this)
                .load(user.getProfilePicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mProfilePicture);
        mCollapsingToolbarLayout.setTitle(user.getUserName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_message){
            Intent intent = new Intent(this, ChatDetailsActivity.class);
            intent.putExtra(ChatDetailsActivity.KEY_CHATS, mUser);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
