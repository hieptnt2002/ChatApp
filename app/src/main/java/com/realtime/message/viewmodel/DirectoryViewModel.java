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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.SearchActivity;


import java.util.ArrayList;
import java.util.List;


public class DirectoryViewModel extends ViewModel {
    MutableLiveData<List<User>> userMutableLiveData;
    FirebaseUser fUser;
    Query query;
    Context context;

    public DirectoryViewModel(Context context) {
        userMutableLiveData = new MutableLiveData<>();
        query = FirebaseDatabase.getInstance().getReference("user").orderByChild("name");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.context = context;
    }

    public MutableLiveData<List<User>> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void getUserDirectoryFromRealtimeDB() {
        List<User> lUser = new ArrayList<>();
        if (fUser != null) {
            String fID = fUser.getUid();
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lUser.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (!user.getUid().equals(fID)) {
                            lUser.add(user);
                        }
                    }
                    userMutableLiveData.setValue(lUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public void onClickSearch(){
        context.startActivity(new Intent(context, SearchActivity.class));
    }
}
