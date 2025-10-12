package com.example.smart_roller_blind;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.smart_roller_blind.Methods.Get_object;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.publish;
import static com.example.smart_roller_blind.Methods.set_theme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class Speed_Activity extends AppCompatActivity {
    SeekBar seekBar;
    View ButtonStop;
    boolean tab = false;
    private Object_model object_model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_speed);
        seekBar = findViewById(R.id.seekBar);
        ButtonStop = findViewById(R.id.ButtonStop);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                ViewGroup.LayoutParams layoutParams = ButtonStop.getLayoutParams();
//                layoutParams.width = i;
//                publish
//                ButtonStop.setLayoutParams(layoutParams);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(tab){
                    object_model.setSpeed_up(seekBar.getProgress());
                }else {
                    object_model.setSpeed_down(seekBar.getProgress());
                }
                publish(String.valueOf(seekBar.getProgress()), "/sub_SPEED", object_model);
            }
        });

        Intent intent = getIntent();
        object_model = Get_object(intent);

        if(tab){
            seekBar.setProgress(object_model.getSpeed_up());
        }else {
            seekBar.setProgress(object_model.getSpeed_down());
        }
    }

    public void ClicStop(View view) {
        publish("0", "/sub_SET_HOME", object_model);
    }

    public void ClicUp(View view) {
        tab = true;
        seekBar.setProgress(object_model.getSpeed_up());
        publish("1", "/sub_SET_HOME", object_model);
    }

    public void ClicDown(View view) {
        tab = false;
        publish("2", "/sub_SET_HOME", object_model);
        seekBar.setProgress(object_model.getSpeed_down());
    }

    public void ClicNext(View view) {
        publish("0", "/sub_SET_HOME", object_model);
        Put_object(this, Set_top_activity.class,object_model);
    }
}