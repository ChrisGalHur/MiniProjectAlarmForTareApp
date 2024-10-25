package com.chrisgalhur.alarmtareapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.chrisgalhur.alarmtareapp.Activity.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtener el PowerManager y adquirir un WakeLock
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "AlarmApp:WakeLock");

        // Adquirir el WakeLock durante un tiempo limitado
        wakeLock.acquire(10 * 60 * 1000L /*10 minutos*/);

        // Iniciar AlarmActivity
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(alarmIntent);

        // Liberar el WakeLock
        wakeLock.release();
    }

    private PendingIntent getDismissIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("DISMISS");
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0;
        return PendingIntent.getBroadcast(context, 0, intent, flags);
    }
}