package com.realtime.message.viewmodel;

import static com.realtime.message.constant.Constant.STATUS_NOTIFICATION;
import static com.realtime.message.constant.Constant.TOGGLE_NOTIFICATION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.MainActivity;
import com.realtime.message.R;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.ProfileActivity;
import com.realtime.message.view.activity.SplashScreen;

public class IndividualViewModel extends BaseObservable {
    public MutableLiveData<User> userLiveData;
    private DatabaseReference reference;
    Activity mActivity;
    SharedPreferences spDarkMode;
    SharedPreferences.Editor editor;
    public ObservableBoolean switchMode = new ObservableBoolean();

    public IndividualViewModel(Activity activity) {
        userLiveData = new MutableLiveData<>();
        reference = FirebaseDatabase.getInstance().getReference("user");
        this.mActivity = activity;
        spDarkMode = mActivity.getSharedPreferences("DARK", Context.MODE_PRIVATE);
        switchMode.set(spDarkMode.getBoolean("night", false));
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void getCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            reference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    userLiveData.setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần thiết
                }
            });
        }
    }

    public void onClickOpenProfile() {
        Intent intent = new Intent(mActivity, ProfileActivity.class);
        mActivity.startActivity(intent);
    }

    public void onClickLogout() {
        AlertDialog.Builder dialogLogout = new AlertDialog.Builder(mActivity);
        dialogLogout.setTitle("Đăng xuất");
        dialogLogout.setCancelable(false);
        dialogLogout.setIcon(R.drawable.logout);
        dialogLogout.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogLogout.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                mActivity.startActivity(new Intent(mActivity, SplashScreen.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        dialogLogout.create().show();

    }

    public void toggleNotifications() {
        AlertDialog.Builder dialogToggle = new AlertDialog.Builder(mActivity);
        dialogToggle.setTitle("Âm thanh và thông báo");
        dialogToggle.setIcon(R.drawable.ring);
        String[] dialogItemsStatus = {
                "Bật",
                "Tắt"};
        dialogToggle.setItems(dialogItemsStatus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor spe = mActivity.getSharedPreferences(TOGGLE_NOTIFICATION, Context.MODE_PRIVATE).edit();
                switch (which) {
                    case 0:
                        spe.putInt(STATUS_NOTIFICATION, 0);
                        break;
                    case 1:
                        spe.putInt(STATUS_NOTIFICATION, 1);
                        break;
                }
                spe.apply();
            }
        });

        dialogToggle.show();
    }

    @SuppressLint("CommitPrefEdits")
    public void switcherSetDarkMode() {
        mActivity.finish();
        boolean darkMode = spDarkMode.getBoolean("night", false);
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor = spDarkMode.edit();
            editor.putBoolean("night", false);
            switchMode.set(false);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor = spDarkMode.edit();
            editor.putBoolean("night", true);
            switchMode.set(true);

        }
        editor.apply();
        Intent intent = new Intent(mActivity, SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivity.startActivity(intent);

    }


}
