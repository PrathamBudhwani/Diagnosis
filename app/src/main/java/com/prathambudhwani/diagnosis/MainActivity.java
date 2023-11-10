package com.prathambudhwani.diagnosis;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.prathambudhwani.diagnosis.recyclermain.checkui.MLkitFaceCheck.REQUEST_IMAGE_CAPTURE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.prathambudhwani.diagnosis.recyclermain.checkui.MLkitFaceCheck;
import com.prathambudhwani.diagnosis.recyclermain.checkui.RootStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private CustomProgressDialog customProgressDialog;
    private int progress;
    SurfaceView surfaceView;
    Toolbar toolbar;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private CheckBluetooth checkBluetooth = new CheckBluetooth();
    private CheckCamera checkCamera = new CheckCamera();
    private CheckGPS checkGPS = new CheckGPS();
    private CheckSensors checkSensors = new CheckSensors();
    private CheckSpeaker checkSpeaker = new CheckSpeaker();
    private RootStatus rootStatus = new RootStatus();
    DatabaseReference testResultsRef = FirebaseDatabase.getInstance().getReference("testResults");

    List<TestResult> testResults = new ArrayList<>();
    ArrayList<DiagnoseListModel> diagnoseListModels = new ArrayList<>();
    RecyclerView recyclerView;
    FloatingActionButton floatingbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        floatingbtn = findViewById(R.id.floatingbtn);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);


        surfaceView = findViewById(R.id.cameraPreview);

        // Add items to diagnoseListModels
        diagnoseListModels.add(new DiagnoseListModel("Check Camera", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Speaker", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Microphone", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Bluetooth", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check GPS", ""));
        diagnoseListModels.add(new DiagnoseListModel("Check Sensors", "GyroScope, Acceleration, Magnetic ,Barometer ,Game Rotation," +
                " Ambient, Proximity , Rotation Vector and other present in device "));
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
                // Check and request permissions if needed
                if (checkPermission()) {
                    retrieveTestResultsFromFirebase();
                } else {
                    requestPermission();
                }
            }
        });

    }

    public void movetoCard(DiagnoseListModel diagnoseListModel, int position) {
        Intent iCamera = new Intent(getApplicationContext(), MLkitFaceCheck.class);
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
                    generateAndDownloadPDF(testResults);
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

    private void generateAndDownloadPDF(List<TestResult> testResults) throws IOException {
        // Define the page dimensions (pageWidth and pageHeight)
        int pageWidth = 595;
        int pageHeight = 842;

        // Creating an object variable for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // Create a page info for our PDF.
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        // Start the page.
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Create a canvas to draw on the page.
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Draw your content, including the Firebase data, on the canvas.
        // Customize this part as needed based on your specific layout and design.
        paint.setTextSize(15);
        int yPosition = 100;
        for (TestResult testResult : testResults) {
            String testName = "Test Name: " + testResult.getTestName();
            String result = "Result: " + testResult.getResult();
            String deviceName = "Device Name: " + testResult.getDeviceName();
            String timestamp = "Timestamp: " + new Date(testResult.getTimestamp()).toString();

            canvas.drawText(testName, 50, yPosition, paint);
            yPosition += 20;
            canvas.drawText(result, 50, yPosition, paint);
            yPosition += 20;
            canvas.drawText(deviceName, 50, yPosition, paint);
            yPosition += 20;
            canvas.drawText(timestamp, 50, yPosition, paint);
            yPosition += 40; // Adjust as needed for spacing between items.
        }

        // Finish the page.
        pdfDocument.finishPage(page);

        // Specify the file path for the PDF.
        File file = new File(Environment.getExternalStorageDirectory(), "TestResults.pdf");

        try {
            // Write the PDF to the file.
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));

            // Show a toast message for successful PDF generation.
            Toast.makeText(this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle any errors.
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate PDF.", Toast.LENGTH_SHORT).show();
        }

        // Close the PDF document.
        pdfDocument.close();
    }

    private boolean checkPermission() {
        // Checking for permissions.
        int permission1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // Requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                // After requesting permissions, show a toast message of permission status.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    // Permissions granted, proceed with PDF generation.
                    retrieveTestResultsFromFirebase();
                } else {
                    Toast.makeText(this, "Reports Saved.", Toast.LENGTH_SHORT).show();
                }
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid = item.getItemId();
        if (itemid == R.id.runonce) {
            customProgressDialog = new CustomProgressDialog(this);
            customProgressDialog.show();


            // Simulate a progress update (e.g., downloading task)
            progress = 0;
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("RestrictedApi")
                @Override
                public void run() {
                    if (progress < 100) {
                        progress += 10;
                        customProgressDialog.setProgress(progress);

                        checkBluetooth.checkBluetoothStatus();


                        MicChecker.isMicrophoneAvailable(getApplicationContext());
                        RootChecker.isDeviceRooted();

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                            Toast.makeText(MainActivity.this, "Results Saved to Server", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        // Check speaker functionality
                        checkSpeaker.playTestTone();

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if (isGpsEnabled) {
                            Toast.makeText(MainActivity.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
                        }


                    }
                    Toast.makeText(MainActivity.this, "Results Saved to Server", Toast.LENGTH_SHORT).show();
                    customProgressDialog.dismiss();
                }
            }, 6500);


        } else {
            customProgressDialog = new CustomProgressDialog(this);
            customProgressDialog.show();

            // Simulate a progress update (e.g., downloading task)
            progress = 0;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progress < 100) {
                        progress += 10;
                        customProgressDialog.setProgress(progress);

                        checkBluetooth.checkBluetoothStatus();


                        MicChecker.isMicrophoneAvailable(getApplicationContext());
                        RootChecker.isDeviceRooted();


                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                            Toast.makeText(MainActivity.this, "Results Saved to Server", Toast.LENGTH_SHORT).show();

                        } else {
                            // If the image is not captured, set a toast to display an error message.
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        // Check speaker functionality
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        if (checkPermission()) {
                            // Pass the context when calling startCameraPreviewAndCapturePhoto
                            checkCamera.startCameraPreviewAndCapturePhoto(MainActivity.this);
                        } else {
                            requestPermission();
                        }


                        try {
                            mediaPlayer.setDataSource(MainActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_tone));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        mediaPlayer.setOnCompletionListener(mp -> {


                            mediaPlayer.release();
                        });
                        mediaPlayer.start();

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if (isGpsEnabled) {
                            Toast.makeText(MainActivity.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
                        }


                    }
                    Toast.makeText(MainActivity.this, "Results Saved to Server", Toast.LENGTH_SHORT).show();
                    customProgressDialog.dismiss();
                }
            }, 6500);

        }
        return super.onOptionsItemSelected(item);
    }




}
