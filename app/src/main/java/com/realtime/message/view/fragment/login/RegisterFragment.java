package com.realtime.message.view.fragment.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.realtime.message.databinding.FragmentRegisterBinding;
import com.realtime.message.view.activity.LoginActivity;
import com.realtime.message.viewmodel.RegisterViewModel;


public class RegisterFragment extends Fragment {
    RegisterViewModel mRegisterViewModel;
    FragmentRegisterBinding registerBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerBinding = FragmentRegisterBinding.inflate(getLayoutInflater(),container,false);
        mRegisterViewModel = new RegisterViewModel(getActivity());
        registerBinding.setRegisterViewModel(mRegisterViewModel);
        registerBinding.getRegisterViewModel().isRegister.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if( registerBinding.getRegisterViewModel().getIsRegister().get() == true){
                    LoginActivity loginActivity = (LoginActivity) getActivity();
                    loginActivity.loginBinding.viewPager2.setCurrentItem(0);
                }
            }
        });
        changeText();
        return registerBinding.getRoot();
    }
    private void changeText(){
        registerBinding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerBinding.getRegisterViewModel().isVisibility.set(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerBinding.edtFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerBinding.getRegisterViewModel().isVisibility.set(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerBinding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerBinding.getRegisterViewModel().isVisibility.set(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registerBinding.getRegisterViewModel().isVisibility.set(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}