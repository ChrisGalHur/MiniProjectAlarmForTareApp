package com.chrisgalhur.alarmtareapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DismissIntent extends BroadcastReceiver {

    private static final String TAG = "'/'/ DismissIntent";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarma desactivada, cancelando notificación");

        NotificationUtil.cancelNotification(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}