package com.realtime.message.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.realtime.message.R;
import com.realtime.message.databinding.ActivityPfBinding;
import com.realtime.message.model.User;
import com.realtime.message.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    ActivityPfBinding profileBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pf);

        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_pf);
        ProfileViewModel profileViewModel = new ProfileViewModel(this);
        profileBinding.setProfileViewModel(profileViewModel);

        profileBinding.getProfileViewModel().getCurrentUser();
        profileBinding.getProfileViewModel().getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
               if(user != null)  {
                    profileBinding.setUser(user);
               }
               }
        });

        profileBinding.getProfileViewModel().getImageUriLiveData().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
            }
        });
        profileBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, profileBinding.getUser().getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileBinding.getProfileViewModel().onActivityResult(requestCode,resultCode,data);
    }

}