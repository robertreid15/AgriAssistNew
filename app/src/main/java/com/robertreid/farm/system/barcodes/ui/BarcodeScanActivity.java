package com.robertreid.farm.system.barcodes.ui;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.robertreid.farm.system.barcodes.model.Item;

import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BarcodeScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion >= Build.VERSION_CODES.M && !checkPermission()) {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0) {
            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraAccepted) {
                Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(CAMERA)) {
                    showMessageOKCancel(
                            (dialog, which) -> requestPermissions(new String[]{CAMERA},
                                    REQUEST_CAMERA));
                }
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void handleResult(Result rawResult) {
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setNeutralButton("Cancel", (dialog, which) -> mScannerView.resumeCameraPreview(BarcodeScanActivity.this));
        builder.setPositiveButton("Add", (dialog, which) -> {
            Item item = new Item();
            item.setValue(rawResult.getText());
            item.setDate(new Date().getTime());
            String pushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
            FirebaseDatabase.getInstance().getReference().child("barcodes").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pushKey)
                    .setValue(item).addOnSuccessListener(aVoid -> finish());

        });

        builder.setMessage(rawResult.getText());
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onResume() {
        super.onResume();

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }


}
