package com.chats.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chats.message.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int STORAGE_PERMISSIONS_CODE = 123;
    public static final int SELECT_PHOTO_CODE = 456;
    // widgets
    private EditText mUserName, mUserEmail;
    private CircleImageView mProfilePicture;
    private Button btnSave;

    // vars
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_node_users));
        mStorageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.ref_profile_pic));
        mUserName = findViewById(R.id.et_user_name);
        mUserEmail = findViewById(R.id.et_user_email);
        mProfilePicture = findViewById(R.id.profile_picture);
        btnSave = findViewById(R.id.save_changes);

        verifyPermissions();
        getUserDetails();
        btnSave.setOnClickListener(this);
        mProfilePicture.setOnClickListener(this);
    }

    public void getUserDetails(){
        mDatabaseReference.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(eventListener);
    }

    private ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    mUserName.setText(user.getUserName());
                    mUserEmail.setText(user.getEmail());
                    if (TextUtils.isEmpty(user.getProfilePicture())) {
                        Glide.with(UserSettingsActivity.this)
                                .load(R.drawable.ic_person)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(mProfilePicture);
                    } else {
                        Glide.with(UserSettingsActivity.this)
                                .load(user.getProfilePicture())
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(mProfilePicture);
                    }
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void verifyPermissions(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED){
            isPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSIONS_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isPermissionGranted = true;
        } else {
            Toast.makeText(this, "Storage permission needed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addValueEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (eventListener != null){
            mDatabaseReference.removeEventListener(eventListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_picture){
            if (isPermissionGranted) {
                selectPhoto();
            } else {
                verifyPermissions();
            }
        } else if (v.getId() == R.id.save_changes){
            saveUserDetails();
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO_CODE);
    }

    private void saveUserDetails() {
        String userName = mUserName.getText().toString().trim();
        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userName").setValue(userName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO_CODE) {
            if (data != null) {
                Uri imageUri = data.getData();
                Glide.with(this)
                        .load(imageUri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(mProfilePicture);
                if (imageUri != null) {
                    uploadImage(imageUri);
                }
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        final StorageReference reference = mStorageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(imageUri.getLastPathSegment());
        UploadTask uploadTask = reference.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri imageUri = task.getResult();
                    if (imageUri != null) {
                        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePicture")
                                .setValue(imageUri.toString());
                    }
                } else {
                    Toast.makeText(UserSettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
