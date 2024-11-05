package com.chrisgalhur.alarmtareapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.chrisgalhur.alarmtareapp.Activity.AlarmActivity;

import java.util.Calendar;

public class AlarmHelper {

    private static String TAG = "AlarmHelper";

    public static void setAlarm(Context context, int hour, int minute) {
        // Recogemos en AlarmManager el servicio de alarmas del sistema
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Creamos un intent con AlarmReceiver que se ejecutará cuando se active pendingIntent
        Intent intent = new Intent(context, AlarmReceiver.class);
        // Creamos un PendingIntent que utilizaremos para programar la alarma con el AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Creamos un objeto Calendar con la hora y minuto seleccionados
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Comprobamos que tenemos el servicio de alarmas
        if (alarmManager != null) {
            try {
                // Si el dispositivo tiene Android 12 o superior, comprobamos si se pueden programar alarmas exactas
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(context, "No se pueden programar alarmas exactas", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Utilizamos setExactAndAllowWhileIdle() setea la alarma exacta y permite que se ejecute en modo idle (modo de bajo consumo)
                    // dandole el pendingIntent para que se ejecute cuando llegue la hora
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Toast.makeText(context, "Alarma programada desde A", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Alarma programada desde A");
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Toast.makeText(context, "Alarma programada desde B", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Alarma programada desde B");
                }
            } catch (SecurityException e) {
                Log.e(TAG, "No se pudo programar la alarma exacta", e);
                Toast.makeText(context, "No se pudo programar la alarma exacta", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
