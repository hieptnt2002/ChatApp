package com.realtime.message.viewmodel;

import static com.realtime.message.constant.Constant.GROUP;
import static com.realtime.message.constant.Constant.NAME_GROUP;
import static com.realtime.message.constant.Constant.URI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realtime.message.R;
import com.realtime.message.model.User;
import com.realtime.message.view.activity.GroupCreateActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserGroupSelectorViewModel extends BaseObservable {
    MutableLiveData<List<User>> userMutableLiveData;
    MutableLiveData<List<User>> userGroupLiveData;
//    MutableLiveData<Uri> uriImgLiveData;

    public ObservableField<String> strSearch = new ObservableField<>();
    FirebaseUser firebaseUser;
    Activity mActivity;

    Uri uri = null;
    public ObservableField<Bitmap> imgBitmap = new ObservableField<>();
    public ObservableField<String> strNameGroup = new ObservableField<>();

    private static final int LIBRARY = 1, CAMERA = 2;

    public UserGroupSelectorViewModel(Activity mActivity) {
        this.mActivity = mActivity;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userMutableLiveData = new MutableLiveData<>();
//        this.uriImgLiveData = new MutableLiveData<>();
        this.userGroupLiveData = new MutableLiveData<>();
        strSearch.set("");
        imgBitmap.set(((BitmapDrawable) mActivity.getResources().getDrawable(R.drawable.doubts)).getBitmap());
    }

    public MutableLiveData<List<User>> getUserMutableLiveData() {
        return userMutableLiveData;
    }

//    public MutableLiveData<Uri> getUriImgLiveData() {
//        return uriImgLiveData;
//    }

    public MutableLiveData<List<User>> getUserGroupLiveData() {
        return userGroupLiveData;
    }


    public void getUserFromRealtimeDatabase(String s) {
        String ss = s.toLowerCase();
        strSearch.set(s);
        List<User> userList = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

    public void onListenerShowPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mActivity);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choosePhotoFromLibrary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();

    }

    private void choosePhotoFromLibrary() {
        Intent intentLibrary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mActivity.startActivityForResult(intentLibrary, LIBRARY);
    }

    private void takePhotoFromCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivity.startActivityForResult(intentCamera, CAMERA);
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == LIBRARY) {
                uri = data.getData();
//                uriImgLiveData.setValue(uri);
            } else if (requestCode == CAMERA) {
                Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
                String path = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmapCamera, "Title", null);
                uri = Uri.parse(path);
//                uriImgLiveData.setValue(uri);
                imgBitmap.set(bitmapCamera);

            }
            if (uri != null) {

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                    imgBitmap.set(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = mActivity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @BindingAdapter("bitmap")
    public static void setBitmap(ImageView imageView, Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void onListenerNextStep() {
        List<User> putListUser = userGroupLiveData.getValue();
        Intent intent = new Intent(mActivity, GroupCreateActivity.class);
        intent.putExtra(URI, uri);
        intent.putExtra(NAME_GROUP, strNameGroup.get());
        intent.putExtra(GROUP, (Serializable) putListUser);
        mActivity.startActivity(intent);
     }

}
