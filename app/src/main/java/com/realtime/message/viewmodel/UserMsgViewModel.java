package com.realtime.message.viewmodel;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.realtime.message.model.ChatList;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class UserMsgViewModel extends ViewModel {

    MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();
    Context context;
    FirebaseUser firebaseUser;
    List<ChatList> userChatList;

    public UserMsgViewModel(Context context) {
        this.context = context;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userChatList = new ArrayList<>();
        updateToken();
    }

    public MutableLiveData<List<User>> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void getUserChat() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChatList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    ChatList chatList = s.getValue(ChatList.class);
                    userChatList.add(chatList);
                }
                getUserFromRealtimeDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserFromRealtimeDatabase() {
        List<User> userList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);

                if (user != null && !firebaseUser.getUid().equals(user.getUid())) {
                    for (ChatList c : userChatList) {
                        if (c.getId().equals(user.getUid())) {
                            userList.add(user);
                        }
                    }
                }
                userMutableLiveData.setValue(userList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);

                if (user != null && !firebaseUser.getUid().equals(user.getUid())) {
                    if (userList != null && !userList.isEmpty()) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (user.getUid() == userList.get(i).getUid()) {
                                userList.set(i, user);
                            }
                        }
                        userMutableLiveData.setValue(userList);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();
                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens");
                tokenRef.child(firebaseUser.getUid()).setValue(token);
            }
        });

    }

    public void onClickSearch() {
        context.startActivity(new Intent(context, SearchActivity.class));
    }


}
