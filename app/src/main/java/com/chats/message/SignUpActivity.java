package com.chats.message;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    // widgets
    private EditText userName, userEmail, userPassword, confirmPassword;

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

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {

        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String confirmationPassword = confirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmationPassword)){
            // sign up user
        } else {
            Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
        }
    }
}
