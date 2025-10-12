package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.Methods.Get_object;
import static com.example.smart_roller_blind.Methods.set_theme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Share extends AppCompatActivity {
    private ImageView qrCodeIV;
    private TextView textView2;
    Object_model object_model;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_share);
        qrCodeIV = findViewById(R.id.idIVQrcode);
        textView2 = findViewById(R.id.textView2);

        object_model = Get_object(getIntent());

        text = "Device/" + object_model.getSup_devise() + "/" + object_model.getName();

        textView2.setText(text);
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        QRGEncoder qrgEncoder = new QRGEncoder(text, null, QRGContents.Type.TEXT, dimen);

            // getting our qrcode in the form of bitmap.
            Bitmap bitmap = qrgEncoder.getBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);

    }

    public void ClicMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void ClicCopy(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("lable", text);
        clipboard.setPrimaryClip(clip);
    }
}