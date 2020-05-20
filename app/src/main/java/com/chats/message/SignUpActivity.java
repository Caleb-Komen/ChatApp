package com.chats.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chats.message.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = SignUpActivity.class.getName();

    // widgets
    private EditText mUserName, mUserEmail, mUserPassword, mConfirmPassword;
    private ProgressBar progressBar;

    // vars
    FirebaseAuth firebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    public DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mUserName = findViewById(R.id.user_name);
        mUserEmail = findViewById(R.id.user_email);
        mUserPassword = findViewById(R.id.user_password);
        mConfirmPassword = findViewById(R.id.user_confirm_password);
        progressBar = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {

        showProgressBar();
        hideSoftKeyboard();

        String email = mUserEmail.getText().toString().trim();
        String password = mUserPassword.getText().toString().trim();
        String confirmationPassword = mConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmationPassword)){
            if (password.equals(confirmationPassword)){
                boolean isValidDomain = confirmEmailDomain(email);
                if (isValidDomain) {
                    // sign up user
                    signUpUser(email, password);
                } else {
                    hideProgressBar();
                    Toast.makeText(this, "Register with valid email domain", Toast.LENGTH_SHORT).show();
                }
            }else {
                hideProgressBar();
                Toast.makeText(this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
            }
        } else {
            hideProgressBar();
            Toast.makeText(this, "Fill out all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    // sign up user
    private void signUpUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressBar();
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Signed Up", Toast.LENGTH_SHORT).show();
                    addUser();
                    firebaseAuth.signOut();
                    redirectToLoginScreen();
                }else {
                    Toast.makeText(SignUpActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Log.d(TAG, "onFailure: Error: " + e);
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                redirectToLoginScreen();
            }
        });
    }

    private boolean confirmEmailDomain(String email){
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        return domain.equals("gmail.com");
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        if (progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void addUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            String userName = mUserName.getText().toString().trim();
            User user = new User();
            user.setUserId(firebaseUser.getUid());
            user.setUserName(userName);
            user.setEmail(firebaseUser.getEmail());
            user.setProfilePicture("");

            mDatabaseReference.child(firebaseUser.getUid()).setValue(user);
        } else {
            Log.d(TAG, "addUser: User logged out");
        }
    }
}
