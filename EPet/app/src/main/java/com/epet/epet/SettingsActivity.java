package com.epet.epet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.epet.epet.backend.business.UserBusiness;
import com.epet.epet.backend.mysql.MySingleton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            EditTextPreference username = findPreference("pref_username");
            username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, final Object newValue) {

                    final UserBusiness userBusiness = new UserBusiness(getContext().getApplicationContext());
                    userBusiness.setUsername(String.valueOf(newValue));
                    return false;
                }
            });
            EditTextPreference phone = findPreference("pref_phone");
            phone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    UserBusiness userBusiness = new UserBusiness(getContext().getApplicationContext());
                    userBusiness.setPhone(String.valueOf(newValue));
                    return false;
                }
            });
            EditTextPreference adress = findPreference("pref_adress");
            adress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    UserBusiness userBusiness = new UserBusiness(getContext().getApplicationContext());
                    userBusiness.setAdress(String.valueOf(newValue));
                    return false;
                }
            });
            Preference deleteaccount = findPreference("pref_deleteaccount");
            deleteaccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    UserBusiness userBusiness = new UserBusiness(getContext().getApplicationContext());
                    userBusiness.deleteAccount();
                    Intent gecisYap = new Intent(getContext(), AuthActivity.class);
                    startActivity(gecisYap);
                    return false;
                }
            });
        }
    }
}