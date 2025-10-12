package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.Methods.set_theme;
import static com.example.smart_roller_blind.MainActivity.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.rm.rmswitch.RMSwitch;
import com.rm.rmswitch.RMTristateSwitch;

public class Settings_Activity extends AppCompatActivity {
    SeekBar seekBar;
    TextView textView;
//    Switch switch1;
    public static int Theme = 1;
    public static boolean Add_Emty_devise = false;

    RMSwitch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        set_theme(Settings_Activity.this);
        setContentView(R.layout.activity_settings);
        textView = findViewById(R.id.textView1);
        textView.setText(String.valueOf(Theme));

        switch1 = (RMSwitch)findViewById(R.id.switch1);
        switch1.setChecked(Add_Emty_devise);




//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
//        mSwitch.setSwitchBkgCheckedColor(typedValue.data);


        switch1.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                Add_Emty_devise = isChecked;
            }
        });


//        switch1 = findViewById(R.id.switch1);
//        switch1.setChecked(Add_Emty_devise);
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Add_Emty_devise = isChecked;
//            }
//        });

    }
    public void ClicMain(View view) {
        startActivity(new Intent(Settings_Activity.this, MainActivity.class));
    }
    public void ClicNext(View view) {
        if(Theme+1<=8){
            Theme++;
            Update();
        }

    }
    public void ClicBack(View view) {
        if(Theme-1>=1){
            Theme--;
            Update();
        }
    }
    public void Update() {
        settings.edit().putInt("Theme", Theme).commit();
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void Clic_del(View view) {
//        DELETE DATABASE
        this.deleteDatabase("Object.db");
    }
}