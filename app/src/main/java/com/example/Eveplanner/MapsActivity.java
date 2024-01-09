package com.example.Eveplanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final String MAPQUEST_API_KEY = "BTjR7udbEih1QrCTjZUq7w1m25Eket6l";
    private static final String MAPQUEST_BASE_URL = "https://www.mapquestapi.com/directions/v2/route?";

    // Database variables
    private EventDatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private MapView mapView;
    private Button searchButton;
    private EditText startEditText;
    private EditText endEditText;
    private ProgressBar loadingIndicator;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isCurrentLocationFound = false;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_maps);
        // Initialize the database
        databaseHelper = new EventDatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        mapView = findViewById(R.id.map_view);
        searchButton = findViewById(R.id.search_button);
        startEditText = findViewById(R.id.start_edit_text);
        endEditText = findViewById(R.id.end_edit_text);
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Set the tile source for the map view
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Enable zoom controls
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Initialize the location manager and listener
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!isCurrentLocationFound) {
                    isCurrentLocationFound = true;

                    // Zoom and center the map to the current location
                    zoomToLocation(new GeoPoint(location.getLatitude(), location.getLongitude()), 10.0);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        // Initialize the geocoder
        geocoder = new Geocoder(this);

        // Set a click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform address search logic here
                String startAddress = startEditText.getText().toString();
                String endAddress = endEditText.getText().toString();
                if (!startAddress.isEmpty() && !endAddress.isEmpty()) {
                    try {
                        List<Address> startAddresses = geocoder.getFromLocationName(startAddress, 1);
                        List<Address> endAddresses = geocoder.getFromLocationName(endAddress, 1);
                        if (!startAddresses.isEmpty() && !endAddresses.isEmpty()) {
                            Address startAddressObj = startAddresses.get(0);
                            Address endAddressObj = endAddresses.get(0);
                            double startLatitude = startAddressObj.getLatitude();
                            double startLongitude = startAddressObj.getLongitude();
                            double endLatitude = endAddressObj.getLatitude();
                            double endLongitude = endAddressObj.getLongitude();

                            GeoPoint startPoint = new GeoPoint(startLatitude, startLongitude);
                            GeoPoint endPoint = new GeoPoint(endLatitude, endLongitude);

                            // Zoom and center the map to the start location
                            zoomToLocation(startPoint, 15.0);

                            // Display the route on the map
                            displayRoute(startPoint, endPoint);
                        } else {
                            Toast.makeText(MapsActivity.this, "Start or end address not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Enter start and end addresses", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void zoomToLocation(GeoPoint geoPoint, double zoomLevel) {
        mapView.getController().setCenter(geoPoint);
        mapView.getController().setZoom(zoomLevel);
    }

    public void displayRoute(GeoPoint startPoint, GeoPoint endPoint) {
        loadingIndicator.setVisibility(View.VISIBLE);

        String url = MAPQUEST_BASE_URL +
                "key=" + MAPQUEST_API_KEY +
                "&from=" + startPoint.getLatitude() + "," + startPoint.getLongitude() +
                "&to=" + endPoint.getLatitude() + "," + endPoint.getLongitude() +
                "&fullShape=true";  // Request full shape

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loadingIndicator.setVisibility(View.GONE);
                            JSONObject route = response.getJSONObject("route");
                            JSONArray shapePoints = route.getJSONObject("shape").getJSONArray("shapePoints");
                            List<GeoPoint> routePoints = new ArrayList<>();
                            for (int i = 0; i < shapePoints.length(); i += 2) {
                                double latitude = shapePoints.getDouble(i);
                                double longitude = shapePoints.getDouble(i + 1);
                                GeoPoint point = new GeoPoint(latitude, longitude);
                                routePoints.add(point);
                            }

                            Polyline routeLine = new Polyline();
                            routeLine.setPoints(routePoints);
                            routeLine.setColor(Color.RED);
                            routeLine.setWidth(10f);
                            mapView.getOverlayManager().add(routeLine);
                            mapView.invalidate();
                        } catch (JSONException e) {
                            loadingIndicator.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingIndicator.setVisibility(View.GONE);
                        Toast.makeText(MapsActivity.this, "Failed to fetch route", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    public boolean isMapDisplayed() {
        return mapView != null && mapView.getVisibility() == View.VISIBLE;
    }
    public LocationListener getLocationListener() {
        return locationListener;
    }
    public void setStartAddress(String startAddress) {
        if (startEditText != null) {
            startEditText.setText(startAddress);
        }
    }
        public void setEndAddress(String endAddress) {
            if (endEditText != null) {
                endEditText.setText(endAddress);
            }
        }
    public EditText getStartAddressView() {
        return startEditText;
    }
    public EditText getEndAddressView() {
        return endEditText;
    }


}

