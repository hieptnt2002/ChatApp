package com.realtime.message.viewmodel;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends BaseObservable {

    MutableLiveData<List<User>> userMutableLiveData;
    MutableLiveData<List<User>> userSearchHistoryLiveData;

    public ObservableField<String> strSearch = new ObservableField<>();
    FirebaseUser firebaseUser;
    DatabaseReference referenceSearch;

    public SearchViewModel() {
        this.userMutableLiveData = new MutableLiveData<>();
        this.userSearchHistoryLiveData = new MutableLiveData<>();
        strSearch.set("");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceSearch = FirebaseDatabase.getInstance().getReference("search");
    }

    public MutableLiveData<List<User>> getUserSearchHistoryLiveData() {
        return userSearchHistoryLiveData;
    }

    public MutableLiveData<List<User>> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void getUserFromRealtimeDatabase(String s) {
        String ss = s.toLowerCase();
        List<User> userList = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("name")
//                .startAt(s).endAt(s + "\uf8ff");
        Query query = FirebaseDatabase.getInstance().getReference("user").orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getUid().equals(firebaseUser.getUid()) && user.getName().toLowerCase().contains(ss))
                        userList.add(user);
                }
                userMutableLiveData.setValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserSearchHistory() {

        List<User> userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        if (firebaseUser != null) {
            referenceSearch.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userList.clear();
                        String uID = dataSnapshot.getValue(String.class);
                        reference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                User user = snapshot.getValue(User.class);
                                if (!firebaseUser.getUid().equals(user.getUid()) && user.getUid().equals(uID)) {
                                    userList.add(user);
                                }
                                userSearchHistoryLiveData.setValue(userList);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                User user = snapshot.getValue(User.class);
                                if (!firebaseUser.getUid().equals(user.getUid()) && user.getUid().equals(uID)) {
                                    for(int i =0; i < userList.size(); i++){
                                        if(user.getUid().equals(userList.get(i).getUid())){
                                            userList.set(i,user);
                                        }
                                    }
                                    userSearchHistoryLiveData.setValue(userList);
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void addHtrSearchRealtimeDb(User user) {
        referenceSearch.child(firebaseUser.getUid()).child(user.getUid()).setValue(user.getUid());
    }
}
