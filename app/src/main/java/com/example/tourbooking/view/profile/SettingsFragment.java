package com.example.tourbooking.view.profile;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.example.tourbooking.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Xử lý click cho các mục Thông tin
        findPreference("company_info").setOnPreferenceClickListener(preference -> {
            startActivity(new android.content.Intent(getContext(),
                    com.example.tourbooking.view.info.CompanyInfoActivity.class));
            return true;
        });
        findPreference("contact").setOnPreferenceClickListener(preference -> {
            startActivity(
                    new android.content.Intent(getContext(), com.example.tourbooking.view.info.ContactActivity.class));
            return true;
        });
        findPreference("faq").setOnPreferenceClickListener(preference -> {
            startActivity(
                    new android.content.Intent(getContext(), com.example.tourbooking.view.info.FAQActivity.class));
            return true;
        });
        findPreference("language_settings").setOnPreferenceClickListener(preference -> {
            startActivity(new android.content.Intent(getContext(),
                    com.example.tourbooking.view.info.LanguageSettingsActivity.class));
            return true;
        });
        findPreference("terms").setOnPreferenceClickListener(preference -> {
            startActivity(
                    new android.content.Intent(getContext(), com.example.tourbooking.view.info.TermsActivity.class));
            return true;
        });
    }
}