package com.github.DenFade.autohcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;

public class AppSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);

        SpannableString s = new SpannableString(getString(R.string.more_info));
        s.setSpan(new URLSpan("https://schoolmenukr.ml/code/app"), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView more = (TextView) findViewById(R.id.more_info);
        more.setText(s);
        more.setMovementMethod(LinkMovementMethod.getInstance());
    }

}