package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class QR_code extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] str = String.valueOf(result.getText()).split("/");
                        try {
//            if (str[0] == "Device") {
                            Object_model object_model = new Object_model(str[2], true, 5, 0, str[1], 200, 300, false, 1000, false, "image", true, 500, 500, "0/%");

                            boolean success = dataBaseHelper.addOne(object_model);
                            if (!success) {
                                Toast.makeText(QR_code.this, R.string.Error_add_devide, Toast.LENGTH_SHORT).show();
                            }
                            startActivity(new Intent(QR_code.this,MainActivity.class));

//            }else{
//                Toast.makeText(QR_code.this, "Error input", Toast.LENGTH_SHORT).show();
//            }
                        }catch (Exception r){
                            Toast.makeText(QR_code.this, R.string.Error_input, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(QR_code.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void ClicMain(View view) {
        startActivity(new Intent(this, Add_device.class));
    }
}