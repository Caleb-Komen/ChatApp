package com.chats.message.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chats.message.LoginActivity;
import com.chats.message.MainAdapter;
import com.chats.message.R;
import com.chats.message.UserSettingsActivity;
import com.chats.message.model.User;
import com.chats.message.ui.chat.ChatsFragment;
import com.chats.message.ui.group.GroupsFragment;
import com.chats.message.ui.group.NewGroupDialog;
import com.chats.message.ui.users.UsersFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    // vars
    FirebaseDatabase mFirebaseDatabase;
    public DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fab = findViewById(R.id.fab);
        setupViewPager();
        viewPager.addOnPageChangeListener(pageChangeListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (viewPager.getCurrentItem()){
                    case 0:
                        Toast.makeText(MainActivity.this, "Invite friend", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Start conversation", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        NewGroupDialog newGroupDialog = new NewGroupDialog();
                        newGroupDialog.show(getSupportFragmentManager(), null);
                        break;
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager(){
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        adapter.addFragments(new UsersFragment(), "Users");
        adapter.addFragments(new ChatsFragment(), "Chats");
        adapter.addFragments(new GroupsFragment(), "Groups");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    fab.setImageResource(R.drawable.ic_add);
                    break;
                case 1:
                    fab.setImageResource(R.drawable.ic_message);
                    break;
                case 2:
                    fab.setImageResource(R.drawable.ic_group_add);
                    break;
                default:
                    super.onPageSelected(position);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile){
            startActivity(new Intent(this, UserSettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "onAuthStateChanged: user logged in");
            }
        }
    };
}
