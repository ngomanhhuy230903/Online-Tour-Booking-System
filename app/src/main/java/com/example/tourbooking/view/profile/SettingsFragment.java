package com.example.tourbooking.view.profile;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.example.tourbooking.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Tại đây, bạn có thể thêm logic để xử lý sự kiện khi một cài đặt thay đổi
        // Ví dụ:
        // findPreference("theme").setOnPreferenceChangeListener((preference, newValue) -> {
        //     boolean isDarkMode = (boolean) newValue;
        //     // Logic để đổi theme app
        //     return true;
        // });
    }
}