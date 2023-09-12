package com.realtime.message.viewmodel;


import static com.realtime.message.constant.Constant.GROUP;
import static com.realtime.message.constant.Constant.NAME_GROUP;
import static com.realtime.message.constant.Constant.URI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.realtime.message.R;
import com.realtime.message.model.User;


import java.io.IOException;
import java.util.List;


public class GroupCreateViewModel extends BaseObservable {
    MutableLiveData<List<User>> userGroupLiveData;
    FirebaseUser fuser;
    public ObservableField<String> strNameGroup = new ObservableField<>();
    public ObservableField<Bitmap> imgBitmap = new ObservableField<>();
    public ObservableField<String> participants = new ObservableField<>();
    Context mContext;
    Uri uri;

    public GroupCreateViewModel( Context mContext) {
        this.mContext = mContext;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        this.userGroupLiveData = new MutableLiveData<>();

    }
    public void setDataFromIntent(Intent mIntent){
        strNameGroup.set(mIntent.getStringExtra(NAME_GROUP));
        uri = mIntent.getParcelableExtra(URI);
        userGroupLiveData.setValue((List<User>) mIntent.getSerializableExtra(GROUP));
        participants.set(getUserGroupLiveData().getValue().size() + " người tham gia");
        convertUriToBitmap();
    }

    public MutableLiveData<List<User>> getUserGroupLiveData() {
        return userGroupLiveData;
    }



    public void convertUriToBitmap() {
        if (uri != null) {
            try {
                imgBitmap.set(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            imgBitmap.set(((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.doubts)).getBitmap());
    }

    @BindingAdapter("bitmap")
    public static void setBitmap(ImageView imageView, Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

}
