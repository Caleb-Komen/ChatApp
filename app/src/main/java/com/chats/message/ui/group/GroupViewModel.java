package com.chats.message.ui.group;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chats.message.model.Group;

import java.util.List;

public class GroupViewModel extends ViewModel {
    private MutableLiveData<List<Group>> mGroups = new MutableLiveData<>();

    public LiveData<List<Group>> getGroups(){
        return mGroups;
    }

    public void setGroups(List<Group> groups){
        mGroups.setValue(groups);
    }
}
