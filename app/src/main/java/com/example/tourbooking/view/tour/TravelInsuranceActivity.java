package com.example.tourbooking.view.tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.InsuranceAdapter;
import com.example.tourbooking.model.Insurance;

import java.util.*;

public class TravelInsuranceActivity extends AppCompatActivity {

    private Spinner spinnerFilterByProvider, spinnerSortByPrice;
    private RecyclerView recyclerInsurancePackages;
    private InsuranceAdapter insuranceAdapter;
    private List<Insurance> insuranceList = new ArrayList<>();
    private List<Insurance> selectedPackages = new ArrayList<>();

    private static final String[] PROVIDERS = {"All", "AIA", "Bảo Việt", "Prudential"};
    private static final String[] SORT_OPTIONS = {"Price Low → High", "Price High → Low"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_insurance);

        spinnerFilterByProvider = findViewById(R.id.spinnerFilterByProvider);
        spinnerSortByPrice = findViewById(R.id.spinnerSortByPrice);
        recyclerInsurancePackages = findViewById(R.id.recyclerInsurancePackages);

        // Setup RecyclerView
        insuranceAdapter = new InsuranceAdapter(insuranceList, selectedPackages -> this.selectedPackages = selectedPackages);
        recyclerInsurancePackages.setLayoutManager(new LinearLayoutManager(this));
        recyclerInsurancePackages.setAdapter(insuranceAdapter);

        // Setup Spinner
        ArrayAdapter<String> providerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PROVIDERS);
        providerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterByProvider.setAdapter(providerAdapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SORT_OPTIONS);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortByPrice.setAdapter(sortAdapter);

        // Spinner listeners
        spinnerFilterByProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterAndSort();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSortByPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterAndSort();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        findViewById(R.id.btnCompare).setOnClickListener(v -> compareSelected());
        findViewById(R.id.btnHelpChat).setOnClickListener(v -> Toast.makeText(this, "Chat support coming soon!", Toast.LENGTH_SHORT).show());
        findViewById(R.id.textTerms).setOnClickListener(v -> Toast.makeText(this, "Terms & Conditions", Toast.LENGTH_SHORT).show());
        findViewById(R.id.textFAQ).setOnClickListener(v -> Toast.makeText(this, "Frequently Asked Questions", Toast.LENGTH_SHORT).show());

        loadMockData(); // Replace with Firebase later
    }

    private void filterAndSort() {
        String selectedProvider = spinnerFilterByProvider.getSelectedItem().toString();
        String sortOption = spinnerSortByPrice.getSelectedItem().toString();

        List<Insurance> filtered = new ArrayList<>();
        for (Insurance pkg : insuranceList) {
            if (selectedProvider.equals("All") || pkg.getProvider().equals(selectedProvider)) {
                filtered.add(pkg);
            }
        }

        if (sortOption.contains("Low")) {
            filtered.sort(Comparator.comparingDouble(Insurance::getPrice));
        } else {
            filtered.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        }

        insuranceAdapter.updateList(filtered);
    }

    private void compareSelected() {
        if (selectedPackages.size() < 2) {
            Toast.makeText(this, "Select at least 2 packages to compare", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CompareInsuranceActivity.class);
        intent.putExtra("selectedPackages", new ArrayList<>(selectedPackages));
        startActivity(intent);
    }


    private void loadMockData() {
        insuranceList.add(new Insurance("Basic Travel Protect", "AIA", 120000));
        insuranceList.add(new Insurance("Premium Trip Guard", "Prudential", 220000));
        insuranceList.add(new Insurance("Family Package", "Bảo Việt", 180000));
        insuranceList.add(new Insurance("Student Explorer", "AIA", 100000));
        insuranceAdapter.updateList(insuranceList);
    }
}
