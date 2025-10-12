package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.MainActivity.client;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.Methods.Get_object;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.internetIsConnected;
import static com.example.smart_roller_blind.Methods.set_theme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;

public class Name_add_Activity extends AppCompatActivity {
    private Object_model object_model;
    EditText mEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_name_add2);
        mEdit = (EditText)findViewById(R.id.editText);

        Intent intent = getIntent();
        object_model = Get_object(intent);
    }

    public void Clicnext(View view) {
        AWSIotConnectionStatus a = client.getConnectionStatus();
        if(a.name() != "DISCONNECTED") {
            object_model.setName(mEdit.getText().toString());
            Put_object(this, Speed_Activity.class, object_model);
        }else{
            if(internetIsConnected()) {
                Toast.makeText(this, "Conecting...", Toast.LENGTH_SHORT).show();
                try {
                    client.connect();
                } catch (AWSIotException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "No Internet Conection", Toast.LENGTH_SHORT).show();
            }

        }



    }
}