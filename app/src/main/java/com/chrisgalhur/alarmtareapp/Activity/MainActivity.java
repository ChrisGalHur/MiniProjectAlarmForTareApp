package com.chrisgalhur.alarmtareapp.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chrisgalhur.alarmtareapp.AlarmHelper;
import com.chrisgalhur.alarmtareapp.R;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "'/'/ MainActivity";
    private ActivityResultLauncher<String> requestAlarmPermissionLauncher;
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;
    private AlarmManager alarmManager;
    private EditText hora;
    private EditText minute;
    private Button btnSetAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        hora = findViewById(R.id.etHoraMain);
        minute = findViewById(R.id.etMinMain);
        btnSetAlarm = findViewById(R.id.btExecuteMain);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !areNotificationsEnabled()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                new AlertDialog.Builder(this)
                        .setTitle("Optimización de Batería")
                        .setMessage("Para asegurar el correcto funcionamiento de la aplicación, es necesario excluirla de las optimizaciones de batería. ¿Deseas continuar?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS); //Lleva directamente al usuario a aceptar el permiso
                            intent.setData(Uri.parse("package:" + getPackageName())); //
                            startActivity(intent);
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }

        btnSetAlarm.setOnClickListener(v -> {
            try {
                int hour = Integer.parseInt(hora.getText().toString());
                int min = Integer.parseInt(minute.getText().toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    Log.d(TAG, "Permiso de alarma exacta no está habilitado.");
                } else {
                    AlarmHelper.setAlarm(this, hour, min);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Formato de hora inválido", e);
                Toast.makeText(this, "Por favor, introduce un formato válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }
}