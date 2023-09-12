package com.realtime.message.model;


import android.graphics.drawable.Drawable;
import android.util.Patterns;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class User implements Serializable {
    String uid;
    String avatar ;
    String name ;
    String phone;
    String email ;

    String status;
    String password;
    public User(){

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String uid, String avatar, String name) {
        this.uid = uid;
        this.avatar = avatar;
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String image) {
        this.avatar = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isValidEmail(){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public boolean isValidPassword(){
        return password.length() <= 24;
    }
    public boolean isValidPhone(){
        return Patterns.PHONE.matcher(phone).matches();
    }

    @BindingAdapter({"imgUrl", "urlError"})
    public static void loadImage(CircleImageView img, String url, Drawable error) {
        Glide.with(img.getContext()).load(url).error(error).into(img);
    }
}
