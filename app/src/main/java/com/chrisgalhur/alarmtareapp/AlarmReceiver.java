package com.chrisgalhur.alarmtareapp;

import static com.chrisgalhur.alarmtareapp.NotificationUtil.createNotificationChannel;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.chrisgalhur.alarmtareapp.Activity.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {
    // BroadcastReceiver permite a la aplicación responder a eventos del sistema, como alarmas programadas, notificaciones, etc.
    // Crea un hilo de ejecución para manejar el evento en el método onReceive().

    private static final String TAG = "AlarmReceiver";
    private static final String CHANNEL_ID = "channel_alarm";
    // WakeLock permite a la aplicación mantener el dispositivo despierto mientras se muestra la notificación
    private static PowerManager.WakeLock wakeLock;

    // Actúa cuando se recibe una alarma programada
    @Override
    public void onReceive(Context context, Intent intent) {
        // Creamos powerManager para obtener el servicio de energía del sistema
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // Con WakeLock mantememos la pantalla encendida, utilizando powerManager para obtener el servicio de energía del sistema
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "AlarmApp:WakeLock");

        // Adquirir el WakeLock y mantenerlo activo hasta que la notificación se muestre
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(10 * 60 * 1000L /*10 minutos*/);
        }

        Log.d(TAG, "Alarma recibida, mostrando notificación");

        //Creamos el canal de notificación que nos servirá para que ese canal haga de puente entre la alarma y la notificación
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.star_wars);

        Intent dismissIntent = new Intent(context, DismissIntent.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationUtil.createNotificationChannel(context, CHANNEL_ID, "Alarm Channel", "Channel for Alarm Notifications", soundUri);
        NotificationUtil.showNotification(context, CHANNEL_ID, R.drawable.ic_alarm, "¡Alarma!", "¡Despierta!", soundUri, dismissPendingIntent);
        // Si la aplicación tiene permiso para mostrar notificaciones, se muestra la notificación
        releaseWakeLock();
    }

    public static void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}