package com.example.smart_roller_blind;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.smart_roller_blind.Methods.Get_object;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.publish;
import static com.example.smart_roller_blind.Methods.set_theme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Set_top_activity extends AppCompatActivity {
    private Object_model object_model;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_set_top);
        button = findViewById(R.id.view21);
        Intent intent = getIntent();
        object_model = Get_object(intent);

    }
    public void ClicStop(View view) {
        publish("0", "/sub_SET_HOME", object_model);
    }

    public void ClicUp(View view) {
        publish("1", "/sub_SET_HOME", object_model);
    }

    public void ClicDown(View view) {
        publish("2", "/sub_SET_HOME", object_model);
    }

    public void ClicNext(View view) {
        publish("4", "/sub_SET_HOME", object_model);
        Put_object(this, Set_bottom_activity.class,object_model);
    }

    public void ClicAutoPosition(View view) {
        publish("3", "/sub_SET_HOME", object_model);
    }
}