package com.github.DenFade.autohcheck;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

public class HcheckService extends Service {

    private String edu;
    private String schoolName;
    private String schoolCode;
    private String realName;
    private String birth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //포그라운드 서비스 전용 Notification 발생
        startForeground(1, new NotificationCompat.Builder(this, getString(R.string.notification_id))
                .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_MIN).build());

        //HcheckClient 에 넣을 정보 가져오기
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(HcheckService.this);
        edu = pref.getString("edu", "");
        schoolName = pref.getString("schoolName", "");
        schoolCode = pref.getString("schoolCode", "");
        realName = pref.getString("realName", "");
        birth = pref.getString("birth", "");

        if(edu.equals("") || schoolName.equals("") || schoolCode.equals("") || realName.equals("") || birth.equals("")){
            //TODO: 설정창으로 이동하는 PendingIntent 구현할것!
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_id))
                    .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.icon)
                    .setContentTitle(getString(R.string.empty_data))
                    .setContentText(getString(R.string.empty_data_sol))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(NotificationManager.class);
            manager.notify(101, builder.build());

            stopSelf();
        }
    }
}
