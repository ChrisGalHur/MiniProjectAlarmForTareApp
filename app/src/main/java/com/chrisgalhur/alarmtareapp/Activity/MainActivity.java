package com.chrisgalhur.alarmtareapp.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
    private TextView scheduleExactAlarmStatus;
    private TextView ignoreBatteryOptimizationsStatus;
    private TextView postNotificationsStatus;
    private TextView wakeLockStatus;
    private TextView vibrateStatus;

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
        scheduleExactAlarmStatus = findViewById(R.id.tvScheduleAlarmPermissionMain);
        ignoreBatteryOptimizationsStatus = findViewById(R.id.tvRequestIgnoreBatteryOptimizationsMain);
        postNotificationsStatus = findViewById(R.id.tvPostNotificationsMain);
        wakeLockStatus = findViewById(R.id.tvWakeLockMain);
        vibrateStatus = findViewById(R.id.tvVibrateMain);

        updatePermissionsStatus();

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
                    Toast.makeText(this, "Alarma programada para " + hour + ":" + min, Toast.LENGTH_SHORT).show();
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

    private void updatePermissionsStatus() {
        //Schedule exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean isGranted = ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).canScheduleExactAlarms();
            setPermissonStatus(scheduleExactAlarmStatus, isGranted);
        } else {
            setPermissonStatus(scheduleExactAlarmStatus, true); //Se considera que el permiso está concedido en versiones anteriores
        }

        //Request ignore battery optimizations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isGranted = pm.isIgnoringBatteryOptimizations(getPackageName());
            setPermissonStatus(ignoreBatteryOptimizationsStatus, isGranted);
        } else {
            setPermissonStatus(ignoreBatteryOptimizationsStatus, true);
        }

        //Post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            setPermissonStatus(postNotificationsStatus, isGranted);
        } else {
            setPermissonStatus(postNotificationsStatus, true); //Se considera que el permiso está concedido en versiones anteriores a TIRAMISU
        }

        //Wake lock
        boolean isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
        setPermissonStatus(wakeLockStatus, isGranted);

        //Vibrate
        isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
        setPermissonStatus(vibrateStatus, isGranted);
    }

    private void setPermissonStatus(TextView textView, boolean isGranted) {
        if (isGranted) {
            textView.setText("YES");
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            textView.setText("NO");
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }
}