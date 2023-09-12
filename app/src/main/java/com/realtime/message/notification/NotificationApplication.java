package com.realtime.message.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.realtime.message.R;

public class NotificationApplication extends Application {
    public static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotify();
    }

    private void createNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri uri = Uri.parse("android.resource://"+ getPackageName() +"/"+ R.raw.sound_notification);
            NotificationChannel notificationChannel =new NotificationChannel(CHANNEL_ID,"PUSH NOTIFICATION", NotificationManager.IMPORTANCE_DEFAULT);
            AudioAttributes audioAttributes = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            notificationChannel.setSound(uri,audioAttributes);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
