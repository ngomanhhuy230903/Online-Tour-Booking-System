package com.example.tourbooking.view.info;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.FAQAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FAQActivity extends AppCompatActivity {

    private final List<FAQAdapter.FAQItem> allFaqs = new ArrayList<>();
    private FAQAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Sample data
        seedData();

        RecyclerView recyclerView = findViewById(R.id.faqList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FAQAdapter(new ArrayList<>(allFaqs));
        recyclerView.setAdapter(adapter);

        EditText searchInput = findViewById(R.id.searchFAQInput);
        Spinner categorySpinner = findViewById(R.id.categoriesFilter);
        TextView noResults = findViewById(R.id.noResultsMessage);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.faq_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        // Listeners
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(searchInput.getText().toString(), categorySpinner.getSelectedItem().toString(), noResults); }
            @Override public void afterTextChanged(Editable s) {}
        });

        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) { filter(searchInput.getText().toString(), categorySpinner.getSelectedItem().toString(), noResults); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        Button contactSupport = findViewById(R.id.contactSupportLink);
        contactSupport.setOnClickListener(v -> {
            // TODO: open support activity or compose email
            android.widget.Toast.makeText(this, "Contact support clicked", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void filter(String query, String category, TextView noResults) {
        String q = query.toLowerCase(Locale.getDefault());
        List<FAQAdapter.FAQItem> filtered = new ArrayList<>();
        for (FAQAdapter.FAQItem item : allFaqs) {
            boolean matchesCat = category.equals("All") || item.category.equalsIgnoreCase(category);
            boolean matchesQuery = q.isEmpty() || item.question.toLowerCase(Locale.getDefault()).contains(q) || item.answer.toLowerCase(Locale.getDefault()).contains(q);
            if (matchesCat && matchesQuery) {
                filtered.add(item);
            }
        }
        adapter.update(filtered);
        noResults.setVisibility(filtered.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void seedData() {
        allFaqs.add(new FAQAdapter.FAQItem("How do I book a tour?", "Select a tour, choose dates and proceed to payment.", "Booking"));
        allFaqs.add(new FAQAdapter.FAQItem("What payment methods are accepted?", "We accept credit cards and PayPal.", "Payment"));
        allFaqs.add(new FAQAdapter.FAQItem("How can I reset my password?", "Use the Forgot Password link on login screen.", "Account"));
        allFaqs.add(new FAQAdapter.FAQItem("Can I cancel a booking?", "Yes, up to 48 hours before departure.", "Booking"));
        allFaqs.add(new FAQAdapter.FAQItem("Is my payment secure?", "All payments are processed via secure gateways.", "Payment"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 