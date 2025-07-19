package com.example.tourbooking.view.tour;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tourbooking.R;
import com.example.tourbooking.model.ItineraryItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class MapTourActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List<ItineraryItem> itineraryList;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLatLng;
    private final String apiKey = "AIzaSyC5NZVY6wJAJ0IESmTw1U-wiP85FH_rydQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_tour);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        String json = getIntent().getStringExtra("itinerary");
        itineraryList = new Gson().fromJson(json, new TypeToken<List<ItineraryItem>>() {}.getType());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                drawRoute();
            } else {
                Toast.makeText(this, "Couldn't get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute() {
        List<LatLng> waypointList = new ArrayList<>();
        for (ItineraryItem item : itineraryList) {
            LatLng point = convertLocationNameToLatLng(item.getLocation());
            if (point != null) waypointList.add(point);
        }

        if (waypointList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy địa điểm hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build URL
        String origin = currentLatLng.latitude + "," + currentLatLng.longitude;
        String destination = waypointList.get(waypointList.size() - 1).latitude + "," + waypointList.get(waypointList.size() - 1).longitude;

        StringBuilder waypointsStr = new StringBuilder();
        for (int i = 0; i < waypointList.size() - 1; i++) {
            LatLng wp = waypointList.get(i);
            waypointsStr.append(wp.latitude).append(",").append(wp.longitude).append("|");
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin +
                "&destination=" + destination +
                "&waypoints=" + waypointsStr +
                "&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject resObj = new JSONObject(response);
                JSONArray routes = resObj.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline");
                    String points = overviewPolyline.getString("points");
                    List<LatLng> decodedPath = decodePolyline(points);

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(decodedPath)
                            .color(getResources().getColor(R.color.purple_500))
                            .width(10f);
                    mMap.addPolyline(polylineOptions);

                    // Add markers
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    for (LatLng latLng : waypointList) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Stop"));
                    }

                    // Fit all points
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(currentLatLng);
                    for (LatLng latLng : waypointList) builder.include(latLng);

                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error drawing route", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Failed to load route", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private LatLng convertLocationNameToLatLng(String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            lng += dlng;

            poly.add(new LatLng(lat / 1E5, lng / 1E5));
        }
        return poly;
    }
}
