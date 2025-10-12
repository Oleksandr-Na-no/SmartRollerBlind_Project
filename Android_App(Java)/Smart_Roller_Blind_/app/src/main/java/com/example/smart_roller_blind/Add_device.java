package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.MainActivity.client;
import static com.example.smart_roller_blind.MainActivity.clientId;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.MainActivity.open2;
import static com.example.smart_roller_blind.MainActivity.settings;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.internetIsConnected;
import static com.example.smart_roller_blind.Methods.set_theme;
import static com.example.smart_roller_blind.Settings_Activity.Add_Emty_devise;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Add_device extends AppCompatActivity {
    EditText mEdit;
    EditText mEdit2;
    EditText editCode;
    private Object_model object_model;
    static boolean add = false;
    static public String USSD = "Network name";
    static public String PASSWORD = "Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_add_device);

        mEdit = (EditText) findViewById(R.id.editText);
        mEdit2 = (EditText) findViewById(R.id.editText2);
        editCode = (EditText) findViewById(R.id.editCode);
        mEdit.setText(USSD);
        mEdit2.setText(PASSWORD);
        mEdit2.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            Clic1(new View(Add_device.this));
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("JavascriptInterface")
    public void Clic1(View view) {
        if (!Add_Emty_devise) {
            String str = editCode.getText().toString();
            if (!str.equals("Code") && !str.equals("") && !str.equals("Code ")) {
                addcode(str);
            } else {
                addnew();
            }
        } else {
            addnull();
        }
    }

    private void addnew() {
        add = true;
        USSD = String.valueOf(mEdit.getText());
        PASSWORD = String.valueOf(mEdit2.getText());
        settings.edit().putString("USSD", USSD).commit();
        settings.edit().putString("PASSWORD", PASSWORD).commit();
        startActivity(new Intent(Add_device.this, Loading.class));
    }

    private void addcode(String str) {
        try {
            String[] strmas = str.split("/");
            if (strmas[0].equals("Device")) {
                Object_model object_model = new Object_model(strmas[2], true, 5, 0, strmas[1], 200, 300, false, 1000, false, "image", true, 500, 500,"1,21,34,1,1;3,13,45,0,0");
                boolean success = dataBaseHelper.addOne(object_model);
                if (success) {
                    open2 = false;
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    Toast.makeText(this, R.string.Error_add_devide, Toast.LENGTH_SHORT).show();
                    addnew();
                }
            } else {
                Toast.makeText(this, R.string.Error_input, Toast.LENGTH_SHORT).show();
                addnew();
            }
        } catch (Exception r) {
            Toast.makeText(this, R.string.Error_input, Toast.LENGTH_SHORT).show();
            addnew();
        }
    }

    private void addnull() {
        try {
            //add new null devise
            object_model = new Object_model("No Name", true, 128, 0, "sub", 200, 300, true, 1000, true, "image", true, 500, 500,"1,21,34,1,1;3,13,45,0,0");
            add = true;
            ///next activity
            Put_object(Add_device.this, Name_add_Activity.class, object_model);

//            Toast.makeText(Add_device.this, object_model.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //if Erorr
            Toast.makeText(Add_device.this, R.string.Error_add_devide, Toast.LENGTH_SHORT).show();
        }
    }

    public void Clic3(View view) {
        startActivity(new Intent(Add_device.this, MainActivity.class));
    }

    public void ClicCode(View view) {
        startActivity(new Intent(Add_device.this, QR_code.class));
    }
}
