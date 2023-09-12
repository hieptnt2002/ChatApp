package com.realtime.message.notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.realtime.message.R;
import com.realtime.message.view.activity.MessageActivity;

import java.util.Map;

public class MyFirebaseMessageService extends FirebaseMessagingService {
    //    Bitmap mBitmap = null;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String sented = message.getData().get("sented");
        String user = message.getData().get("user");
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences pref = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUid = pref.getString("currentuser", "none");

        if (fuser != null && sented.equals(fuser.getUid()) && !user.equals(currentUid)) {
            senNotification(message);
        }
    }

    private void senNotification(RemoteMessage message) {
        Map<String, String> map = message.getData();
        String user = map.get("user");
        String icon = map.get("icon");
        String title = map.get("title");
        String body = map.get("body");

//        convert url to bitmap

//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE); // Không lưu cache để đảm bảo lấy được bitmap mới nhất
//
//
//        Glide.with(this)
//                .asBitmap()
//                .load("https://firebasestorage.googleapis.com/v0/b/messageapps-a634e.appspot.com/o/uploads%2F1690275279339.jpg?alt=media&token=284e8d50-0b38-4370-831c-318dafb95475")
//                .apply(requestOptions)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
//
//                        mBitmap = bitmap;
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                       mBitmap = null;
//                    }
//                });
//

//        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(user.replaceAll("\\D", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("user", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound_notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationApplication.CHANNEL_ID)
                .setSmallIcon(Integer.parseInt(icon))
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(uri)
                .setContentIntent(pendingIntent);
//        if (mBitmap != null){
//            builder.setLargeIcon(mBitmap);
//            builder.setSmallIcon(bitmapToIcon());
//        } else
//            builder.setSmallIcon(Integer.parseInt(icon));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int i = 0;
        if (j > 0) {
            i = j;
        }
        notificationManager.notify(i, builder.build());
    }

//    public IconCompat bitmapToIcon() {
//        // Tạo một IconCompat từ Bitmap
//        IconCompat iconCompat = IconCompat.createWithBitmap(mBitmap);
//        return iconCompat;
//    }
}
