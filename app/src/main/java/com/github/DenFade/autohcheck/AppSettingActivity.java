package com.github.DenFade.autohcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AppSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);

        TextView instant = (TextView) findViewById(R.id.instant_submit);
        TextView more = (TextView) findViewById(R.id.more_info);

        SpannableString s1 = new SpannableString(getString(R.string.instant_submit));
        s1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Toast.makeText(AppSettingActivity.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                startService(new Intent(AppSettingActivity.this, HcheckDisposableService.class));
            }
        }, 15, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString s2 = new SpannableString(getString(R.string.more_info));
        s2.setSpan(new URLSpan("https://schoolmenukr.ml/code/app"), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        instant.setText(s1);
        instant.setMovementMethod(LinkMovementMethod.getInstance());

        more.setText(s2);
        more.setMovementMethod(LinkMovementMethod.getInstance());
    }

}