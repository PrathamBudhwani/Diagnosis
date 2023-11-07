package com.prathambudhwani.diagnosis;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prathambudhwani.diagnosis.recyclermain.DiagnoseListModel;
import com.prathambudhwani.diagnosis.recyclermain.RecyclerViewMainAdapter;
import com.prathambudhwani.diagnosis.recyclermain.checkui.CheckBluetooth;
import com.prathambudhwani.diagnosis.recyclermain.checkui.CheckCamera;
import com.prathambudhwani.diagnosis.recyclermain.checkui.CheckGPS;
import com.prathambudhwani.diagnosis.recyclermain.checkui.CheckMicrophone;
import com.prathambudhwani.diagnosis.recyclermain.checkui.CheckSensors;
import com.prathambudhwani.diagnosis.recyclermain.checkui.CheckSpeaker;
import com.prathambudhwani.diagnosis.recyclermain.checkui.RootStatus;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseReference testResultsRef = FirebaseDatabase.getInstance().getReference("testResults");

    List<TestResult> testResults = new ArrayList<>();
    ArrayList<DiagnoseListModel> diagnoseListModels = new ArrayList<>();
    RecyclerView recyclerView;
    FloatingActionButton floatingbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingbtn = findViewById(R.id.floatingbtn);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        diagnoseListModels.add(new DiagnoseListModel("Check Camera", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Speaker", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Microphone", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Bluetooth", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check GPS", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Sensors", ""));
        diagnoseListModels.add(new DiagnoseListModel("Root Status", ""));

        RecyclerViewMainAdapter adapter = new RecyclerViewMainAdapter(this, diagnoseListModels, new RecyclerViewMainAdapter.ItemClickListener() {
            @Override
            public void onItemCick(DiagnoseListModel diagnoseListModel, int position) {
                movetoCard(diagnoseListModel, position);
            }
        });
        recyclerView.setAdapter(adapter);

        floatingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveTestResultsFromFirebase();
            }
        });
    }

    public void movetoCard(DiagnoseListModel diagnoseListModel, int position) {
        Intent iCamera = new Intent(getApplicationContext(), CheckCamera.class);
        Intent iSpeaker = new Intent(getApplicationContext(), CheckSpeaker.class);
        Intent iMicrophone = new Intent(getApplicationContext(), CheckMicrophone.class);
        Intent iBluetooth = new Intent(getApplicationContext(), CheckBluetooth.class);
        Intent iGPS = new Intent(getApplicationContext(), CheckGPS.class);
        Intent iSensors = new Intent(getApplicationContext(), CheckSensors.class);
        Intent iRootStatus = new Intent(getApplicationContext(), RootStatus.class);
        switch (position) {
            case 0:
                startActivity(iCamera);
                break;
            case 1:
                startActivity(iSpeaker);
                break;
            case 2:
                startActivity(iMicrophone);
                break;
            case 3:
                startActivity(iBluetooth);
                break;
            case 4:
                startActivity(iGPS);
                break;
            case 5:
                startActivity(iSensors);
                break;
            default:
                startActivity(iRootStatus);
                break;
        }
    }

    private void retrieveTestResultsFromFirebase() {
        testResultsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                testResults.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String testName = snapshot.child("testName").getValue(String.class);
                    String result = snapshot.child("result").getValue(String.class);
                    String deviceName = snapshot.child("deviceName").getValue(String.class);
                    long timestamp = snapshot.child("timestamp").getValue(Long.class);

                    TestResult testResult = new TestResult(testName, result, deviceName, timestamp);
                    testResults.add(testResult);
                }

                try {
                    generateAndDownloadPDF();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database retrieval error
                Toast.makeText(MainActivity.this, "Failed to retrieve test results", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateAndDownloadPDF() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            float y = 700;

            for (TestResult result : testResults) {
                contentStream.beginText();
                contentStream.newLineAtOffset(100, y);
                contentStream.showText("Test Name: " + result.getTestName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Result: " + result.getResult());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Device Name: " + result.getDeviceName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Timestamp: " + result.getTimestamp());
                contentStream.endText();

                y -= 80; // Adjust the Y position for the next entry
            }
        }

        File pdfFile = new File(getCacheDir(), "test_results.pdf");
        document.save(pdfFile);
        document.close();

        Uri pdfUri = FileProvider.getUriForFile(this, "com.prathambudhwani.diagnosis.provider", pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(pdfUri, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            showPdfViewerNotFoundDialog();
        }
    }

    private void showPdfViewerNotFoundDialog() {
        new AlertDialog.Builder(this)
                .setTitle("PDF Viewer Not Found")
                .setMessage("To view the PDF, please install a PDF viewer app.")
                .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Launch the Play Store to search for a PDF viewer app
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader"));
                        try {
                            startActivity(marketIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, "Play Store app not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
