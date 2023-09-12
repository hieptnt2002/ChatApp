package com.realtime.message.viewmodel;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.realtime.message.model.User;
import com.realtime.message.util.CustomProgress;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewModel extends BaseObservable {
    Activity activity;
    DatabaseReference reference;
    StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    ObservableField<String> strUri = new ObservableField<>();
    public MutableLiveData<Uri> imageUriLiveData = new MutableLiveData<>();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();

    static final int IMAGE_REQUEST = 1;

    public MutableLiveData<Uri> getImageUriLiveData() {
        return imageUriLiveData;
    }

    public MutableLiveData<User> getUserLiveData() {
        return userLiveData;
    }

    CustomProgress customProgress;

    public ProfileViewModel(Activity activity) {
        reference = FirebaseDatabase.getInstance().getReference("user");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("avatar");
        this.activity = activity;
        strUri.set(null);
        customProgress = new CustomProgress(activity);
    }

    public void getCurrentUser() {
        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userLiveData.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //


    public void onClickOpenImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImg() {
        customProgress.show();
        if (imageUriLiveData.getValue() != null) {
            Uri imageUri = imageUriLiveData.getValue();

            String fileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            storageReference = FirebaseStorage.getInstance().getReference("uploads/").child(fileName);

//            Tải lên ảnh lên Firebase Storage
            uploadTask = storageReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        customProgress.dismiss();
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        strUri.set(mUri);
                        User user = userLiveData.getValue();
                        user.setAvatar(mUri);
                        userLiveData.setValue(user);
                        customProgress.dismiss();

                    } else {
                        customProgress.dismiss();
                        Toast.makeText(activity, "Failed upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    customProgress.dismiss();
                }
            });
        } else {
            Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show();
             customProgress.dismiss();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUriLiveData.setValue(data.getData());
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(activity, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImg();
            }
        }
    }

    public void onClickSaveProfile() {
        customProgress.show();
        String avatar = strUri.get();
        String name = userLiveData.getValue().getName();
        Map<String, Object> map = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            if (avatar != null && !avatar.isEmpty())
                map.put("avatar", avatar);
            map.put("name", name);
            reference.child(firebaseUser.getUid()).updateChildren(map);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    customProgress.dismiss();
                    activity.onBackPressed();
                }
            }, 1500);
        } else {
            Toast.makeText(activity, "Please enter the required information", Toast.LENGTH_SHORT).show();
            customProgress.dismiss();
        }


    }
}
