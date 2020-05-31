package com.chats.message.ui.group;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.chats.message.R;
import com.chats.message.model.Group;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewGroupDialog extends DialogFragment implements View.OnClickListener {

    public static final int PHOTO_REQUEST_CODE = 123;

    // vars
    private DatabaseReference mDbReference;
    private StorageReference mStorageReference;

    // widgets
    private EditText mEtGroupName;
    private Button mBtnCreate;
    private Button mBtnCancel;
    private ImageView mProfile;
    private String mGroupId;
    private String mProfileUriString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_node_groups));
        mStorageReference = FirebaseStorage.getInstance().getReference()
                .child(getString(R.string.ref_group_profile_pics));
        mGroupId = mDbReference.push().getKey();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_group, container, false);
        mEtGroupName = view.findViewById(R.id.et_group_name);
        mBtnCreate = view.findViewById(R.id.btn_create);
        mBtnCancel = view.findViewById(R.id.btn_cancel);
        mProfile = view.findViewById(R.id.group_profile);

        mBtnCreate.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mProfile.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_create){
            createGroup();
            getDialog().dismiss();
        } else if (v.getId() == R.id.btn_cancel){
            getDialog().dismiss();
        } else if (v.getId() == R.id.group_profile){
            selectPhoto();
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select photo"), PHOTO_REQUEST_CODE);
    }

    private void createGroup() {
        String groupName = mEtGroupName.getText().toString().trim();
        String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Group group = new Group(mGroupId, groupName, creatorId, mProfileUriString);
        mDbReference.child(mGroupId).setValue(group);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (data != null) {
                Uri imageUri = data.getData();
                Glide.with(requireActivity())
                        .load(imageUri)
                        .into(mProfile);
                if (imageUri != null) {
                    saveProfilePicture(imageUri);
                }
            }
        }
    }

    private void saveProfilePicture(final Uri imageUri) {
        final StorageReference reference = mStorageReference.child(mGroupId).child(imageUri.getLastPathSegment());
        UploadTask uploadTask = reference.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    mProfileUriString = task.getResult().toString();
                } else {
                    Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
