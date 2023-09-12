package com.realtime.message.viewmodel;


import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.realtime.message.BR;
import com.realtime.message.model.User;
import com.realtime.message.util.CustomProgress;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends BaseObservable {

    String name = "";
    String phone = "";
    String email = "";
    String password = "";
    public ObservableField<String> messageFailed = new ObservableField<>();
    public ObservableBoolean isRegister = new ObservableBoolean();
    public ObservableBoolean isVisibility = new ObservableBoolean();
    Context mContext;
    User user;
    private FirebaseAuth auth;
    private CustomProgress progressDialog;
    DatabaseReference dbReference;


    public RegisterViewModel(Context context) {
        this.mContext = context;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }


    public ObservableBoolean getIsRegister() {
        return isRegister;
    }

    public void onClickRegister() {
        progressDialog = new CustomProgress(mContext);
//        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        progressDialog.show();
        FirebaseAuth.getInstance().signOut();
        user = new User(email, password);
        if (email.isEmpty() || password.isEmpty() || email == null || password == null ||
                name.isEmpty() || name == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageFailed.set("Please enter the required information");
                    isRegister.set(false);
                    isVisibility.set(false);
                    progressDialog.dismiss();
                }
            }, 1500);
        } else if (!user.isValidEmail() || !user.isValidPassword()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageFailed.set("Please provide valid information");
                    isRegister.set(false);
                    isVisibility.set(false);
                    progressDialog.dismiss();
                }
            }, 1500);
        } else {
            register();

        }
    }

    private void register() {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userid = auth.getCurrentUser().getUid();

                    Map<String,Object> map = new HashMap<>();
                    map.put("uid",userid);
                    map.put("avatar","default");
                    map.put("name",name);
                    map.put("email",email);
                    map.put("phone",phone);
                    map.put("status","default");

                    dbReference = FirebaseDatabase.getInstance().getReference("user").child(userid);
                    dbReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                isRegister.set(true);
                                isVisibility.set(true);
                            }
                        }
                    });

                } else {
                    messageFailed.set("Email already exists");
                    isRegister.set(false);
                    isVisibility.set(false);
                }
                progressDialog.dismiss();
            }
        });
    }
}
