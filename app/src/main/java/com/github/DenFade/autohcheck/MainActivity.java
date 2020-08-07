package com.github.DenFade.autohcheck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private Button on_off_btn;
    private Button setting_btn;
    private boolean setBlinking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.app_title);
        on_off_btn = (Button) findViewById(R.id.app_onoff);
        setting_btn = (Button) findViewById(R.id.app_setting);

        //title 애니메이션 설정
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        setBlinking = pref.getBoolean("blink", false);

        if(setBlinking){
            Animation animation = new AlphaAnimation(1f, 0.3f);
            animation.setDuration(1500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            title.startAnimation(animation);
        }

        //버튼 설정
        on_off_btn.setText(getColoredOnOffText(HcheckService.isRunning()));
        on_off_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HcheckService.class);
                if(HcheckService.isRunning()){
                    on_off_btn.setText(getColoredOnOffText(false));
                    stopService(intent);
                } else {
                    on_off_btn.setText(getColoredOnOffText(true));
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //애니메이션 해제
        if(setBlinking) title.clearAnimation();
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
        SpannableString s = new SpannableString(on_off_btn.getText());
        if (isRun) {
            s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.service_on)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan( ContextCompat.getColor(this, R.color.service_off)), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }
}