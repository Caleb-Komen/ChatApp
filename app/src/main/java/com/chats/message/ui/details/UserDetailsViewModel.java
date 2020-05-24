package com.chats.message.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chats.message.model.User;

public class UserDetailsViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>();

    public void setUser(User user){
        this.user.setValue(user);
    }

    public LiveData<User> getUser(){
        return user;
    }
}
