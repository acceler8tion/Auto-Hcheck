package com.github.DenFade.autohcheck;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.util.Timer;

public class HcheckDisposableService extends Service {

    private final Timer timer = new Timer();

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("HcheckDisposableService", "onCreate 호출");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(HcheckDisposableService.this);
        String edu = pref.getString("edu", "");
        String schoolName = pref.getString("schoolName", "");
        String schoolCode = pref.getString("schoolCode", "");
        String realName = pref.getString("realName", "");
        String birth = pref.getString("birth", "");

        if(edu.equals("") || schoolName.equals("") || schoolCode.equals("") || realName.equals("") || birth.equals("")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_id))
                    .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.round_icon)
                    .setContentTitle(getString(R.string.empty_data_title))
                    .setContentText(getString(R.string.empty_data_text))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AppSettingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            NotificationManager manager = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? (NotificationManager) getSystemService(NOTIFICATION_SERVICE) : getSystemService(NotificationManager.class);
            manager.notify(101, builder.build());

            stopSelf();
        }

        //schedule 호출
        HcheckTask task = new HcheckTask(this, schoolCode, schoolName, realName, birth, edu, true, true);
        timer.schedule(task, 500);

        //Service 종료
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d("HcheckDisposableService", "onDestroy 호출");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
