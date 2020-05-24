package com.chats.message.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chats.message.model.Message;

import java.util.List;

public class ChatDetailsViewModel extends ViewModel {
    private MutableLiveData<Message> mMessage = new MutableLiveData<>();

    public void setMessages(Message message){
        mMessage.setValue(message);
    }

    public LiveData<Message> getMessages(){
        return mMessage;
    }
}
