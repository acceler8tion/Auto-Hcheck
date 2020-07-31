package com.github.DenFade.autohcheck;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.github.DenFade.autohcheck.covid.HcheckClient;
import com.github.DenFade.autohcheck.covid.HcheckRequest;
import com.github.DenFade.autohcheck.exception.HcheckException;

import java.util.Calendar;
import java.util.TimerTask;

import static android.content.Context.NOTIFICATION_SERVICE;

public class HcheckTask extends TimerTask {

    private static int notification_id = 103;

    private Context context;
    private String schoolCode;
    private String schoolName;
    private String realName;
    private String birth;
    private String edu;

    HcheckTask(Context context, String schoolCode, String schoolName, String realName, String birth, String edu){
        this.context = context;
        this.schoolCode = schoolCode;
        this.schoolName = schoolName;
        this.realName = realName;
        this.birth = birth;
        this.edu = edu;
    }

    @Override
    public void run() {

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);

        if(hour != 7 || !(30 <= min && min <= 35)) return;

        NotificationManager manager = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE) : context.getSystemService(NotificationManager.class);
        NotificationCompat.Builder builder;

        try{
            HcheckClient client = new HcheckClient(schoolCode, schoolName, realName, birth, edu);
            HcheckRequest request = new HcheckRequest(client);
            request.enter()
                    .authorize()
                    .submit()
                    .end();
        } catch (Exception e){
            e.printStackTrace();

            //Error Notification 생성
            builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_id))
                    .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.round_icon)
                    .setContentTitle(context.getString(R.string.err_on_running_title))
                    .setContentText(String.format("%s : %s", e.getClass().getName(), e.getMessage()))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true);
            manager.notify(notification_id++, builder.build());
            return;
        }

        builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_id))
                .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.round_icon)
                .setContentTitle(context.getString(R.string.suc_on_running_title))
                .setContentText(context.getString(R.string.suc_on_running_text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);
        manager.notify(notification_id++, builder.build());
    }

}
