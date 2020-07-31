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

import java.util.Calendar;
import java.util.Timer;

public class HcheckService extends Service {

    private static boolean running = false;
    private final Timer timer = new Timer();
    private HcheckTask task;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        Log.d("HcheckService", "onCreate 호출");
        super.onCreate();

        //포그라운드 서비스 전용 Notification 발생
        NotificationManager manager = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? (NotificationManager) getSystemService(NOTIFICATION_SERVICE) : getSystemService(NotificationManager.class);
        startForeground(1, new NotificationCompat.Builder(this, getString(R.string.notification_id))
                .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.round_icon)
                .setContentText(getString(R.string.foreground_title))
                .setPriority(NotificationCompat.PRIORITY_MIN).build());

        //HcheckClient 에 넣을 정보 가져오기
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(HcheckService.this);
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
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AppSettingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            manager.notify(101, builder.build());

            stopSelf();
        }

        //타이머 schedule 전 마무리
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = (Calendar) cal.clone();
        cal2.add(Calendar.MINUTE, (5 - cal.get(Calendar.MINUTE)%5) + 5);
        cal2.set(Calendar.SECOND, 0);

        //schedule 호출
        task = new HcheckTask(this, schoolCode, schoolName, realName, birth, edu);
        timer.schedule(task, cal2.getTime(), 300000); //5min
        running = true;

        //알람 호출
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_id))
                .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.round_icon)
                .setContentTitle("매 7:30(AM) 마다 자가진단을 제출합니다.")
                .setContentText(getString(R.string.success_start_text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getService(this, 1, new Intent(this, HcheckDisposableService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        manager.notify(102, builder.build());
    }

    @Override
    public void onDestroy() {
        Log.d("HcheckService", "onDestroy 호출");
        super.onDestroy();

        //스케줄 중지 요청
        task.cancel();
        running = false;
   }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning(){
        return running;
    }

}
