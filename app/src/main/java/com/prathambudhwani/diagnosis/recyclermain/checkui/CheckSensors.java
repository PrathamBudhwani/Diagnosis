package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.TestResult;

import java.util.ArrayList;
import java.util.List;

public class CheckSensors extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");
    ListView sensorListView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sensors);

        sensorListView = findViewById(R.id.sensorListView);

        // Get the system's sensor service
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get a list of all available sensors
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Extract sensor names
        List<String> sensorNames = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            sensorNames.add(sensor.getName());
        }

        // Create an adapter for the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorNames);

        // Set the adapter to the list view
        sensorListView.setAdapter(adapter);
        // Create a TestResult object
        TestResult testResult = new TestResult();
        testResult.setTestName("Sensors Test");
        testResult.setResult("Pass");
        testResult.setTimestamp(System.currentTimeMillis());
        testResult.setDeviceName(Build.MODEL);

        // Push the result to Firebase
        String key = testResultsRef.push().getKey();
        testResultsRef.child(key).setValue(testResult);
        Toast.makeText(CheckSensors.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
    }
}