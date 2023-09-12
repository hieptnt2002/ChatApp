package com.realtime.message.view.fragment.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.realtime.message.MainActivity;
import com.realtime.message.databinding.FragmentLoginBinding;
import com.realtime.message.view.activity.LoginActivity;
import com.realtime.message.view.adapter.OnActivityResultListener;
import com.realtime.message.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment implements OnActivityResultListener {
    FragmentLoginBinding mFragmentLoginBinding;
    LoginViewModel mLoginViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((LoginActivity) getActivity()).setOnActivityResultListener(this);
        mFragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);

        mLoginViewModel = new LoginViewModel(getActivity());
        mFragmentLoginBinding.setLoginViewModel(mLoginViewModel);
        mFragmentLoginBinding.getLoginViewModel().getIsLogin().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (mFragmentLoginBinding.getLoginViewModel().getIsLogin().get() == true) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });
        onChangeText();
        return mFragmentLoginBinding.getRoot();
    }

    void onChangeText() {
        mFragmentLoginBinding.edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFragmentLoginBinding.getLoginViewModel().isVisibility.set(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mFragmentLoginBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFragmentLoginBinding.getLoginViewModel().isVisibility.set(true);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        mFragmentLoginBinding.getLoginViewModel().onActivityResult(requestCode, resultCode, data);
    }
}