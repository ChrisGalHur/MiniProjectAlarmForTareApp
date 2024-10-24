package com.chrisgalhur.alarmtareapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmHelper {

    private static String TAG = "AlarmHelper";

    public static void setAlarm(Context context, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(settingsIntent);
                Log.d(TAG, "Solicitar permiso para programar alarmas exactas");
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                Log.d(TAG, "Alarma programada");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al programar la alarma" + e.getMessage());
            // Manejar la excepción de seguridad aquí
        }

        Toast.makeText(context, "Alarma programada", Toast.LENGTH_SHORT).show();
    }
}
