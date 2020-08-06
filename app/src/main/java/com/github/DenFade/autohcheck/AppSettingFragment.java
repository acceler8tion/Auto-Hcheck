package com.github.DenFade.autohcheck;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AppSettingFragment extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_setting, rootKey);

        PreferenceManager manager = getPreferenceManager();

        EditTextPreference schoolName = (EditTextPreference) manager.findPreference(getString(R.string.schoolName_key));
        EditTextPreference schoolCode = manager.findPreference(getString(R.string.schoolCode_key));
        EditTextPreference realName = manager.findPreference(getString(R.string.realName_key));
        EditTextPreference birth = manager.findPreference(getString(R.string.birth_key));
        EditTextPreference edu = manager.findPreference(getString(R.string.edu_key));


        birth.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint(getString(R.string.birth_hint));
            }
        });
        edu.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {

            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setHint(getString(R.string.edu_hint));
            }
        });

        manager.getSharedPreferences().getAll().forEach((k, v) -> {
            if(!v.equals("")) getPreferenceScreen().findPreference(k).setSummary((String) v);
        });

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Preference pref = getPreferenceScreen().findPreference(s);
                String data = sharedPreferences.getString(s, "");
                if(data.equals("")){
                    switch (s){
                        case "schoolName":
                            pref.setSummary(getString(R.string.schoolName_sum));
                            break;
                        case "schoolCode":
                            pref.setSummary(getString(R.string.schoolCode_sum));
                            break;
                        case "realName":
                            pref.setSummary(getString(R.string.realName_sum));
                            break;
                        case "birth":
                            pref.setSummary(getString(R.string.birth_sum));
                            break;
                        case "edu":
                            pref.setSummary(getString(R.string.edu_sum));
                            break;
                        default:
                    }
                } else {
                    pref.setSummary(data);
                }
            }
        };

        manager.getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }
}