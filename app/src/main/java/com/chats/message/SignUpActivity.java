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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = SignUpActivity.class.getName();

    // widgets
    private EditText userName, userEmail, userPassword, confirmPassword;
    private ProgressBar progressBar;

    // vars
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        confirmPassword = findViewById(R.id.user_confirm_password);
        progressBar = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {

        showProgressBar();
        hideSoftKeyboard();

        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String confirmationPassword = confirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmationPassword)){
            if (password.equals(confirmationPassword)){
                boolean isValidDomain = confirmEmailDomain(email);
                if (isValidDomain) {
                    // sign up user
                    signUpUser(email, password);
                } else {
                    Toast.makeText(this, "Register with valid email domain", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
            }
        } else {
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
}
