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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Alarm extends AppCompatActivity {
    Spinner spinner;
    RecyclerView recyclerView1;
    public static Object_model object_model;
    RecyclerViewAdapter_Alarms adapter_alarm;

    List<Alarms> everyone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        set_theme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        recyclerView1 = findViewById(R.id.idCourseRV);

        object_model = Get_object(getIntent());

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);// in this method '2' represents number of columns to be displayed in grid view.
        recyclerView1.setLayoutManager(layoutManager);

        adapter_alarm = new RecyclerViewAdapter_Alarms(object_model.getAlarm(), this);
        recyclerView1.setAdapter(adapter_alarm);//set adapter to recycler view.


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                    final int fromPos = viewHolder.getAdapterPosition();
//                    final int toPos = viewHolder.getAdapterPosition();
//                    // move item in `fromPos` to `toPos` in adapter.



                return true;// true if moved, false otherwise
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int pos = viewHolder.getLayoutPosition();
                object_model.del_one_alarm(pos);
                dataBaseHelper.update_one(object_model);
////                adapter_alarm.notifyItemRemoved(pos);
                adapter_alarm.removeItem(pos);
                publish(object_model.getAlarmStr_for_esp(), "/sub_ALARM", Alarm.object_model);

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView1);
    }

    public void Clic3(View view) {
        Put_object(this,Device_Activity.class,object_model);
    }

    boolean isKeyboardShowing = false;
    void onKeyboardVisibilityChanged(boolean opened) {

    }

    public void ClicAdd(View view) {
        if(adapter_alarm.getItemCount() <= 10) {
            Calendar calendar = Calendar.getInstance();
//        object_model.add_one_alarm(new Alarms(1,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true,true));
//        dataBaseHelper.update_one(object_model);
            adapter_alarm.add_one(new Alarms(1, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true, true));
            publish(object_model.getAlarmStr_for_esp(), "/sub_ALARM", Alarm.object_model);
        }else{
            Toast.makeText(this, R.string.Max_alarms, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

}