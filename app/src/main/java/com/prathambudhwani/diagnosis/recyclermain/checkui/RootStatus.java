package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.RootChecker;
import com.prathambudhwani.diagnosis.TestResult;

public class RootStatus extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");
    TextView statusText;
    Button checkButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_status);

        statusText = findViewById(R.id.statusText);
        checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRootStatus(view);

                // Create a TestResult object
                TestResult testResult = new TestResult();
                testResult.setTestName("Root Test");
                testResult.setResult(RootChecker.isDeviceRooted() ? "Fail" : "Pass");
                testResult.setTimestamp(System.currentTimeMillis());
                testResult.setDeviceName(Build.MODEL);

                // Push the result to Firebase
                String key = testResultsRef.push().getKey();
                testResultsRef.child(key).setValue(testResult);
                Toast.makeText(RootStatus.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkRootStatus(View view) {
        // You should call your RootChecker to check if the device is rooted.
        // Assuming RootChecker.isDeviceRooted() returns a boolean.
        boolean isRooted = RootChecker.isDeviceRooted();
        if (isRooted) {
            statusText.setText("Root Status: Rooted");
        } else {
            statusText.setText("Root Status: Not Rooted");
        }
    }
}
