package com.example.tourbooking.view.info;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tourbooking.R;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {
    private Spinner languageList;
    private TextView currentSelectionHighlight, previewSampleText, restartAppPrompt;
    private Switch rtlSupportToggle;
    private Button cancelButton, saveButton;
    private LinearLayout bottomButtons;
    private int selectedLangIndex = 0;
    private boolean rtlEnabled = false;
    private final String[] sampleTexts = {
            "Hello, how are you?",
            "Hola, ¿cómo estás?",
            "Bonjour, comment ça va?",
            "Xin chào, bạn khỏe không?"
    };
    private final String[] langCodes = { "en", "es", "fr", "vi" };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        languageList = findViewById(R.id.languageList);
        currentSelectionHighlight = findViewById(R.id.currentSelectionHighlight);
        previewSampleText = findViewById(R.id.previewSampleText);
        rtlSupportToggle = findViewById(R.id.rtlSupportToggle);
        restartAppPrompt = findViewById(R.id.restartAppPrompt);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);
        bottomButtons = findViewById(R.id.bottomButtons);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageList.setAdapter(adapter);

        languageList.setSelection(selectedLangIndex);
        updateSelection(selectedLangIndex);

        languageList.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedLangIndex = position;
                updateSelection(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        rtlSupportToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rtlEnabled = isChecked;
            showRestartPromptIfNeeded();
        });

        cancelButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> {
            // Lưu lựa chọn ngôn ngữ và RTL (giả lập, thực tế cần lưu vào SharedPreferences)
            setLocale(langCodes[selectedLangIndex], rtlEnabled);
            showRestartPromptIfNeeded();
        });
    }

    private void updateSelection(int position) {
        String[] langs = getResources().getStringArray(R.array.languages);
        currentSelectionHighlight.setText("Selected: " + langs[position]);
        previewSampleText.setText(sampleTexts[position]);
    }

    private void showRestartPromptIfNeeded() {
        restartAppPrompt.setVisibility(View.VISIBLE);
    }

    private void setLocale(String langCode, boolean rtl) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        if (rtl) {
            config.setLayoutDirection(locale);
        } else {
            config.setLayoutDirection(Locale.ENGLISH);
        }
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}