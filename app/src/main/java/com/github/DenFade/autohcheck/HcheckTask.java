package com.github.DenFade.autohcheck;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.github.DenFade.autohcheck.covid.HcheckClient;
import com.github.DenFade.autohcheck.covid.HcheckRequest;
import com.github.DenFade.autohcheck.exception.HcheckException;

import java.util.Calendar;
import java.util.TimerTask;

import static android.content.Context.NOTIFICATION_SERVICE;

public class HcheckTask extends TimerTask {

    private static int notification_id = 103;

    private final Context context;
    private final String schoolCode;
    private final String schoolName;
    private final String realName;
    private final String birth;
    private final String edu;
    private final boolean alert;
    private final boolean disposable;
    private final Handler handler = new Handler();

    HcheckTask(Context context, String schoolCode, String schoolName, String realName, String birth, String edu, boolean alert, boolean disposable){
        this.context = context;
        this.schoolCode = schoolCode;
        this.schoolName = schoolName;
        this.realName = realName;
        this.birth = birth;
        this.edu = edu;
        this.alert = alert;
        this.disposable = disposable;
    }

    @Override
    public void run() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if(alert) Toast.makeText(context, "Checking..", Toast.LENGTH_SHORT).show();
            }
        });

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);

        if(!disposable && (hour != 7 || !(30 <= min && min <= 35))) return;

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

            NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
            style.bigText(e.getMessage());
            style.setSummaryText(e.getClass().getName());

            //Error Notification 생성
            builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_id))
                    .setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ? R.mipmap.app_icon_round : R.drawable.round_icon)
                    .setContentTitle(context.getString(R.string.err_on_running_title))
                    .setStyle(style)
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
