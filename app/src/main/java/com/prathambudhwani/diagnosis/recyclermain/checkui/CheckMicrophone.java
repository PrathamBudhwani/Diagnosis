package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.MicChecker;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.TestResult;

public class CheckMicrophone extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");
    TextView statusText;
    Button checkButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_microphone);

        statusText = findViewById(R.id.statusText);
        checkButton = findViewById(R.id.checkButton);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMicrophone(view);

                // Create a TestResult object
                TestResult testResult = new TestResult();
                testResult.setTestName("Microphone Test");
                testResult.setResult(MicChecker.isMicrophoneAvailable(getApplicationContext()) ? "Pass" : "Fail");
                testResult.setTimestamp(System.currentTimeMillis());
                testResult.setDeviceName(Build.MODEL);

                // Push the result to Firebase
                String key = testResultsRef.push().getKey();
                testResultsRef.child(key).setValue(testResult);
                Toast.makeText(CheckMicrophone.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkMicrophone(View view) {
        Context context = getApplicationContext();
        boolean isMicAvailable = MicChecker.isMicrophoneAvailable(context);
        boolean isMicFunctional = MicChecker.isMicrophoneFunctional();

        if (isMicAvailable && isMicFunctional) {
            statusText.setText("Microphone Status: Available and Functional");
        } else if (isMicAvailable) {
            statusText.setText("Microphone Status: Available but Not Functional");
        } else {
            statusText.setText("Microphone Status: Not Available");
        }
    }
}