package com.realtime.message.viewmodel;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.model.Message;
import com.realtime.message.view.activity.UserGroupSelectorActivity;

import java.util.HashMap;
import java.util.Map;

public class MainViewModel extends ViewModel {
    DatabaseReference reference;
    FirebaseUser fuser;
    MutableLiveData<Integer> unreadLiveData;
    Context mContext;


    public MainViewModel(Context mContext) {
        reference = FirebaseDatabase.getInstance().getReference("user");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        unreadLiveData = new MutableLiveData<>();
        this.mContext = mContext;
    }

    public MutableLiveData<Integer> getUnreadLiveData() {
        countChats();
        return unreadLiveData;
    }

    public void status(String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.child(fuser.getUid()).updateChildren(map);
    }

    public void countChats() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread = 0;
                for (DataSnapshot s : snapshot.getChildren()) {
                    Message chat = s.getValue(Message.class);
                    if (fuser.getUid().equals(chat.getReceiver()) && !chat.getIsseen()) {
                        unread++;
                    }
                }
                unreadLiveData.setValue(unread);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onClickImgGroup(){
        mContext.startActivity(new Intent(mContext, UserGroupSelectorActivity.class));
    }
}
