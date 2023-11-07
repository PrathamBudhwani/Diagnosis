package com.prathambudhwani.diagnosis;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

public class CustomProgressDialog extends Dialog {

    private ProgressBar progressBar;

    public CustomProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress_dialog);

        progressBar = findViewById(R.id.progressBar);
        setCancelable(false); // Optional: To prevent users from canceling the dialog
    }

    // Method to set the progress value
    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    // You can add other methods to update the dialog as needed
}
