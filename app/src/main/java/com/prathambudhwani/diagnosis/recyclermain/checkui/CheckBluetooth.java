package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.TestResult;

public class CheckBluetooth extends AppCompatActivity {
    // Initialize Firebase

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");


    Button btncheck;
    CardView btcard;
    TextView btresult;
    BluetoothAdapter myBluetoothAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_bluetooth);

        btncheck = findViewById(R.id.btncheck);
        btcard = findViewById(R.id.btcard);
        btresult = findViewById(R.id.btresult);

        // Remove the duplicate declaration here
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            myBluetoothAdapter = bluetoothManager.getAdapter();
        }

        btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetoothStatus();

                // Create a TestResult object
                TestResult testResult = new TestResult();
                testResult.setTestName("Bluetooth Test");
                testResult.setResult(checkBluetoothStatus() ? "Pass" : "Fail");
                testResult.setTimestamp(System.currentTimeMillis());
                testResult.setDeviceName(Build.MODEL);
                
                // Push the result to Firebase
                String key = testResultsRef.push().getKey();
                testResultsRef.child(key).setValue(testResult);
                Toast.makeText(CheckBluetooth.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkBluetoothStatus() {
        if (myBluetoothAdapter == null) {
            btresult.setText("Bluetooth not available");
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
            btcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red));
            return false;
        } else {
            if (myBluetoothAdapter.isEnabled()) {
                btresult.setText("Bluetooth is enabled");
                btcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
                return true;
            } else {
                btresult.setText("Bluetooth is disabled");
                btcard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red));
                return false;
            }
        }
    }
}
