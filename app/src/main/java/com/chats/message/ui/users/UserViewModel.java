package com.chats.message.ui.users;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chats.message.model.User;

import java.util.List;

public class UserViewModel extends ViewModel {
    private MutableLiveData<List<User>> users;

    public MutableLiveData<List<User>> getUsers() {
        if (users == null){
            users = new MutableLiveData<>();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users.setValue(users);
    }
}
