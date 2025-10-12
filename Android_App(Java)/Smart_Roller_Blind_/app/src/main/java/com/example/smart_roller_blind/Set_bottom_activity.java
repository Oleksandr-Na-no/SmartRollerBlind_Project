package com.example.smart_roller_blind;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.Methods.Get_object;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.publish;
import static com.example.smart_roller_blind.Methods.set_theme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Set_bottom_activity extends AppCompatActivity {
    private Object_model object_model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_set_bottom);

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

    public void ClicSave(View view) {
        publish("5", "/sub_SET_HOME", object_model);
        if(Add_device.add) {
            boolean success = dataBaseHelper.addOne(object_model);
            if (!success) {
                Toast.makeText(this, R.string.Error_add_devide, Toast.LENGTH_SHORT).show();
            }
            Add_device.add = false;
            startActivity(new Intent(this,MainActivity.class));
        }else{
            dataBaseHelper.update_one(object_model);
            Put_object(this,Device_Activity.class,object_model);
        }
    }
}