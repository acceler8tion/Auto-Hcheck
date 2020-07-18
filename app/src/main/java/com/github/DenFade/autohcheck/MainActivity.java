package com.github.DenFade.autohcheck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private Button onoff_btn;
    private Button setting_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.app_title);
        onoff_btn = (Button) findViewById(R.id.app_onoff);
        setting_btn = (Button) findViewById(R.id.app_setting);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channelForService = new NotificationChannel(
                getString(R.string.notification_id),
                getString(R.string.notification_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channelForService.setDescription(getString(R.string.notification_desc));
        notificationManager.createNotificationChannel(channelForService);
    }
}