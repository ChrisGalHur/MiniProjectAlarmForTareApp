package com.chrisgalhur.alarmtareapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.chrisgalhur.alarmtareapp.Activity.MainActivity;

public class NotificationUtil {

    private static final int ALARM_NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "alarm_channel";

    public static void createNotificationChannel(Context context, String channelId, String channelName, String channelDescription, Uri soundUri) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        NotificationChannel existingChannel = notificationManager.getNotificationChannel(channelId);
        if (existingChannel != null) {
            notificationManager.deleteNotificationChannel(channelId);
        }

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);

        channel.setSound(soundUri, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        channel.enableVibration(true);
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(channel);
    }

    public static void showNotification(Context context, String channelId, int iconResId, String title, String content, Uri soundUri, PendingIntent dismissIntent) {
        Intent intentReturnMain = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ALARM_NOTIFICATION_ID, intentReturnMain, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        if (dismissIntent != null) {
            builder.addAction(0, "Dismiss", dismissIntent);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ALARM_NOTIFICATION_ID);
    }
}