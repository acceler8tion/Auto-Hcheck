package com.github.DenFade.autohcheck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

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

        onoff_btn.setText(getColoredOnOffText(HcheckService.isRunning()));
        onoff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HcheckService.class);
                if(HcheckService.isRunning()){
                    onoff_btn.setText(getColoredOnOffText(false));
                    stopService(intent);
                } else {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        onoff_btn.setText(getColoredOnOffText(true));
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                }

            }
        });

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AppSettingActivity.class);
                startActivity(intent);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel channelForService = new NotificationChannel(
                getString(R.string.notification_id),
                getString(R.string.notification_name),
                NotificationManager.IMPORTANCE_HIGH
        );
        channelForService.setDescription(getString(R.string.notification_desc));
        notificationManager.createNotificationChannel(channelForService);
    }

    private CharSequence getColoredOnOffText(boolean isRun){
        SpannableString s = new SpannableString(onoff_btn.getText());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (isRun) {
                s.setSpan(new ForegroundColorSpan(getColor(R.color.service_on)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(Color.BLACK), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(getColor(R.color.service_off)), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }
}