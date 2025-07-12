package com.example.tourbooking.view.info;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tourbooking.R;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactActivity extends AppCompatActivity {
    private EditText nameInput, emailInput, subjectInput, messageTextarea, phoneInput;
    private Button sendButton, resetButton;
    private MapView mapView;
    private GoogleMap googleMap;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        subjectInput = findViewById(R.id.subjectInput);
        messageTextarea = findViewById(R.id.messageTextarea);
        phoneInput = findViewById(R.id.phoneInput);
        sendButton = findViewById(R.id.sendButton);
        resetButton = findViewById(R.id.resetButton);
        mapView = findViewById(R.id.embeddedMap);

        // MapView setup
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap gMap) {
                googleMap = gMap;
                MapsInitializer.initialize(ContactActivity.this);
                LatLng companyLocation = new LatLng(21.028511, 105.804817); // Hà Nội
                googleMap.addMarker(new MarkerOptions().position(companyLocation).title("Our Office"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(companyLocation, 15f));
            }
        });

        sendButton.setOnClickListener(v -> handleSend());
        resetButton.setOnClickListener(v -> handleReset());
    }

    private void handleSend() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String subject = subjectInput.getText().toString().trim();
        String message = messageTextarea.getText().toString().trim();
        // phone is optional

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(subject)
                || TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Gửi thông tin liên hệ lên server hoặc email
        Toast.makeText(this, "Your message has been sent!", Toast.LENGTH_LONG).show();
        handleReset();
    }

    private void handleReset() {
        nameInput.setText("");
        emailInput.setText("");
        subjectInput.setText("");
        messageTextarea.setText("");
        phoneInput.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}