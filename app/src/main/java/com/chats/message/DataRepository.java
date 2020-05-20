package com.chats.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.chats.message.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class DataRepository {
    private MutableLiveData<List<User>> users;
    public LiveData<List<User>> getUsers(){
        return users;
    }

    private void retrieveUsers(){

    }
}
