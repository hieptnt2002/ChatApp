package com.realtime.message.viewmodel;

import android.graphics.drawable.Drawable;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.realtime.message.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRadioViewModel {
    User user;
    boolean checked;

    public UserRadioViewModel(User user, boolean checked) {
        this.user = user;
        this.checked = checked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    @BindingAdapter({"imgUrl", "urlError"})
    public static void loadImage(CircleImageView img, String url, Drawable error) {
        Glide.with(img.getContext()).load(url).error(error).into(img);
    }
}
