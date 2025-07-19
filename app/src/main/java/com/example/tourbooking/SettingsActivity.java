package com.example.tourbooking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageOption, privacyOption, themeOption;
    private Switch emailToggle, smsToggle, pushToggle, mfaSwitch;
    private Button saveSettingsButton, resetDefaultsButton;
    private TextView supportLink;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "TourBookingPrefs";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_EMAIL = "email_notifications";
    private static final String KEY_SMS = "sms_notifications";
    private static final String KEY_PUSH = "push_notifications";
    private static final String KEY_PRIVACY = "privacy";
    private static final String KEY_MFA = "mfa";
    private static final String KEY_THEME = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        languageOption = findViewById(R.id.language_option);
        privacyOption = findViewById(R.id.privacy_option);
        themeOption = findViewById(R.id.theme_option);
        emailToggle = findViewById(R.id.email_toggle);
        smsToggle = findViewById(R.id.sms_toggle);
        pushToggle = findViewById(R.id.push_toggle);
        mfaSwitch = findViewById(R.id.mfa_switch);
        saveSettingsButton = findViewById(R.id.save_settings_button);
        resetDefaultsButton = findViewById(R.id.reset_defaults_button);
        supportLink = findViewById(R.id.support_link);

        // Language spinner
        ArrayAdapter<CharSequence> langAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageOption.setAdapter(langAdapter);

        // Privacy spinner
        ArrayAdapter<CharSequence> privacyAdapter = ArrayAdapter.createFromResource(this,
                R.array.privacy_options, android.R.layout.simple_spinner_item);
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privacyOption.setAdapter(privacyAdapter);

        // Theme spinner
        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(this,
                R.array.theme_options, android.R.layout.simple_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeOption.setAdapter(themeAdapter);

        loadSettings();

        saveSettingsButton.setOnClickListener(v -> saveSettings());
        resetDefaultsButton.setOnClickListener(v -> resetSettings());
        supportLink.setOnClickListener(v -> openSupportLink());
    }

    private void loadSettings() {
        languageOption.setSelection(prefs.getInt(KEY_LANGUAGE, 0));
        privacyOption.setSelection(prefs.getInt(KEY_PRIVACY, 0));
        themeOption.setSelection(prefs.getInt(KEY_THEME, 0));
        emailToggle.setChecked(prefs.getBoolean(KEY_EMAIL, false));
        smsToggle.setChecked(prefs.getBoolean(KEY_SMS, false));
        pushToggle.setChecked(prefs.getBoolean(KEY_PUSH, true));
        mfaSwitch.setChecked(prefs.getBoolean(KEY_MFA, false));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LANGUAGE, languageOption.getSelectedItemPosition());
        editor.putInt(KEY_PRIVACY, privacyOption.getSelectedItemPosition());
        editor.putInt(KEY_THEME, themeOption.getSelectedItemPosition());
        editor.putBoolean(KEY_EMAIL, emailToggle.isChecked());
        editor.putBoolean(KEY_SMS, smsToggle.isChecked());
        editor.putBoolean(KEY_PUSH, pushToggle.isChecked());
        editor.putBoolean(KEY_MFA, mfaSwitch.isChecked());
        editor.apply();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private void resetSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
        loadSettings();
        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show();
    }

    private void openSupportLink() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@example.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
        try {
            startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}