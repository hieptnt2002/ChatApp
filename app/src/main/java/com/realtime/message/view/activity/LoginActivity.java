package com.realtime.message.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.realtime.message.R;
import com.realtime.message.databinding.ActivityLoginBinding;
import com.realtime.message.view.adapter.LoginViewPager2Adapter;
import com.realtime.message.view.adapter.OnActivityResultListener;
import com.realtime.message.view.fragment.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    public ActivityLoginBinding loginBinding;
    LoginViewPager2Adapter adapter;
    private OnActivityResultListener listener;

    public void setOnActivityResultListener(OnActivityResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        adapter = new LoginViewPager2Adapter(this);
        loginBinding.viewPager2.setAdapter(adapter);
//        viewPager2.setUserInputEnabled(false);
        loginBinding.viewPager2.setCurrentItem(0, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            listener.onResult(requestCode,resultCode,data);
        }
    }
}