package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.MainActivity.adapter;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.MainActivity.recyclerView;
import static com.example.smart_roller_blind.Methods.Get_object;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.publish;
import static com.example.smart_roller_blind.Methods.set_theme;
import static com.example.smart_roller_blind.Methods.ubdate_list;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.rmswitch.RMSwitch;

public class Device_Activity extends AppCompatActivity {
    private Object_model object_model;
    SeekBar seekBar;
    View button2;
    View ellipse_1;
    View rectangle_2;
    View rectangle_3;
    EditText name_edit;
    EditText sensor_text;
    EditText edit_sensor;
    RMSwitch switch1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set_theme(this);
        setContentView(R.layout.activity_device);

        seekBar = findViewById(R.id.seekBar);
        button2 = findViewById(R.id.button2);
        ellipse_1 = findViewById(R.id.ellipse_1);
        rectangle_2 = findViewById(R.id.rectangle_2);
        rectangle_3 = findViewById(R.id.rectangle_3);
        name_edit = findViewById(R.id.name_edit);
        sensor_text = findViewById(R.id.sensor_text);
        edit_sensor = findViewById(R.id.edit_sensor);
        switch1 = findViewById(R.id.switch1);

        object_model = Get_object(getIntent());

        Log.e("IIIIIIIIIIII",object_model.getSup_devise());
//        Log.e("IIIIIIIIIIII", String.valueOf(object_model.getId()));
//        Toast.makeText(this, String.valueOf(object_model.getId()), Toast.LENGTH_SHORT).show();

        update_button();
        name_edit.setText(object_model.getName());
        seekBar.setProgress(object_model.getPosition());
        switch1.setChecked(object_model.getAuto_on());
        sensor_text.setText(Integer.toString(object_model.getSensor_now()));
        edit_sensor.setText(Integer.toString(object_model.getAuto_threshold()));
        switch1.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                if(object_model.getAlarm().size() != 0){
                    object_model.setAuto_on(!object_model.getAuto_on());
                    dataBaseHelper.update_one(object_model);
                    publish("6/" + (object_model.getAuto_on() ? 1 : 0), "/sub_SET_HOME", object_model);
                }
            }
        });

        edit_sensor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    publish(edit_sensor.getText().toString(), "/sub_SENSOR", object_model);
                    object_model.setAuto_threshold(Integer.parseInt(edit_sensor.getText().toString()));
                    dataBaseHelper.update_one(object_model);
                    return false;
                }
                return true;
            }
        });


        name_edit.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                name_edit.setCursorVisible(true);
            }
            public void afterTextChanged(Editable s) {
//                    object_model.setName(name_edit.getText().toString());
//                    dataBaseHelper.update_one(object_model);
                }
            });
        name_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    name_edit.setCursorVisible(true);
                }
            }
        });
        name_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    name_edit.setCursorVisible(false);
                    return false;
                }
                return true;
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar.getProgress()>5){
                    object_model.setOpen(true);
                    update_button();
                }else{
                    object_model.setOpen(false);
                    update_button();
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                publish(String.valueOf(seekBar.getProgress()), "/sub_POSITION", object_model);
//                Toast.makeText(Device_Activity.this, ""+seekBar.getProgress(), Toast.LENGTH_SHORT).show();
                object_model.setPosition(seekBar.getProgress());
                dataBaseHelper.update_one(object_model);
            }
        });
        //Scroll lock
        seekBar.setOnTouchListener(new SeekBar.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle Seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    public void Click(View view) {
        object_model.setOpen(!object_model.getisOpen());
        if(object_model.getisOpen()) {
            ValueAnimator anim = ValueAnimator.ofInt(0, seekBar.getMax());
            anim.setDuration(50);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animProgress = (Integer) animation.getAnimatedValue();
                    seekBar.setProgress(animProgress);
                }
            });
            anim.start();
//            seekBar.setProgress(10);
            object_model.setPosition(10);
        }else{
            ValueAnimator anim = ValueAnimator.ofInt(seekBar.getMax(), 0);
            anim.setDuration(50);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animProgress = (Integer) animation.getAnimatedValue();
                    seekBar.setProgress(animProgress);
                }
            });
            anim.start();
//            seekBar.setProgress(0);
            object_model.setPosition(0);
        }
        dataBaseHelper.update_one(object_model);
        publish(object_model.getisOpen() ? "1" : "0", "/sub", object_model);
    }

    public void Clic1(View view) {
        Async_Task_Delete asyncTask = new Async_Task_Delete();
        asyncTask.execute(object_model);
        startActivity(new Intent(Device_Activity.this,MainActivity.class));
    }
    private void update_button(){
        if(object_model.getisOpen()){
            button2.setBackgroundResource(R.drawable.blok_2);
            ellipse_1.setBackgroundResource(R.drawable.ellipse_1);
            rectangle_2.setBackgroundResource(R.drawable.rectangle_2);
            rectangle_3.setBackgroundResource(R.drawable.rectangle_3);
        }else {
            button2.setBackgroundResource(R.drawable.blok_2_2);
            ellipse_1.setBackgroundResource(R.drawable.ellipse_1_2);
            rectangle_2.setBackgroundResource(R.drawable.rectangle_2_2);
            rectangle_3.setBackgroundResource(R.drawable.rectangle_3_2);
        }
    }

    public void Clic4(View view) {
        Add_device.add = false;
        Add_device.add = false;
        Put_object(this,Speed_Activity.class,object_model);
    }

    public void ClicChare(View view) {
        Put_object(this,Share.class,object_model);
    }

    public void Clic_Alarm(View view) {
        Put_object(this,Alarm.class,object_model);
    }

    private class Async_Task_Delete extends AsyncTask<Object_model, String, String> {
        @Override
        protected String doInBackground(Object_model... strings) {
            Object_model object_model1 = strings[0];
            dataBaseHelper.deleteOne_id(object_model1.getId());
            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemChanged(RecyclerViewAdapter.pos);
                    //adapter.notifyDataSetChanged();
                }
            });
        }
    }
    public void Clic3(View view) {
//        onBackPressed();
        startActivity(new Intent(Device_Activity.this, MainActivity.class));
    }
//    private class Async_Task_Update extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            dataBaseHelper.deleteOne_id(object_model.getId());
//            return "Exexut";
//        }
//    }

}
