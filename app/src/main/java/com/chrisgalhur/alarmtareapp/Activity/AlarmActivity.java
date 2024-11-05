package com.chrisgalhur.alarmtareapp.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.chrisgalhur.alarmtareapp.AlarmReceiver;
import com.chrisgalhur.alarmtareapp.R;


public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Configurar MediaPlayer para reproducir el sonido de alarma
        mediaPlayer = MediaPlayer.create(this, R.raw.star_wars); // Usa un archivo mp3 o wav en res/raw
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Liberar el WakeLock cuando se inicia la actividad por completo
        AlarmReceiver.releaseWakeLock();

        // Configurar botón para detener la alarma
        Button stopAlarmButton = findViewById(R.id.btnStopAlarm);
        stopAlarmButton.setOnClickListener(v -> stopAlarm());
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}