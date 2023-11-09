package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.TestResult;

public class CheckGPS extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // You can choose any value you like.

    Button checkGpsButton;
    TextView gpsStatusText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_gps);

        checkGpsButton = findViewById(R.id.checkGpsButton);
        gpsStatusText = findViewById(R.id.gpsStatusText);

        checkGpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGpsFunctionality();
                // Create a TestResult object
                TestResult testResult = new TestResult();
                testResult.setTestName("GPS Test");
                testResult.setResult(checkGpsFunctionality() ? "Pass" : "Fail");
                testResult.setTimestamp(System.currentTimeMillis());
                testResult.setDeviceName(Build.MODEL);

                // Push the result to Firebase
                String key = testResultsRef.push().getKey();
                testResultsRef.child(key).setValue(testResult);
                Toast.makeText(CheckGPS.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkGpsFunctionality() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGpsEnabled) {
            gpsStatusText.setText("GPS is available and enabled.");
            return true;
        } else {
            gpsStatusText.setText("GPS is not available or not enabled.");
            enableGps();
            return false;
        }

    }

    private void enableGps() {
        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsIntent);
    }

    // Request location permission if it's not granted already.
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
