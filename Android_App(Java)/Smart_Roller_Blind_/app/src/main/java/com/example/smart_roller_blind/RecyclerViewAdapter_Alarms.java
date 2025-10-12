package com.example.smart_roller_blind;


import static android.app.PendingIntent.getActivity;
import static com.example.smart_roller_blind.MainActivity.adapter;
import static com.example.smart_roller_blind.MainActivity.client;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.MainActivity.open2;
import static com.example.smart_roller_blind.MainActivity.recyclerView;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.publish;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.rm.rmswitch.RMSwitch;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_Alarms extends RecyclerView.Adapter<RecyclerViewAdapter_Alarms.RecyclerViewHolder_Alarms> {

    public List<Alarms> getCourseDataArrayList() {
        return courseDataArrayList;
    }

    public void setCourseDataArrayList(List<Alarms> courseDataArrayList) {
        this.courseDataArrayList = courseDataArrayList;
    }

    private List<Alarms> courseDataArrayList;
    private Context mcontext;
    static boolean ubd = true;

    public RecyclerViewAdapter_Alarms(List<Alarms> recyclerDataArrayList, Context mcontext) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder_Alarms onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_alarm_item, parent, false);
        return new RecyclerViewHolder_Alarms(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder_Alarms holder, int position) {
        // Set the data to textview and imageview.
        Alarms alarms = courseDataArrayList.get(position);
//        Object_model object_model = Alarm.object_model;
        int pos = position;
        //////////////////////
        holder.active.setChecked(alarms.getAct());
        holder.time.setText((alarms.getH() < 10 ? "0" + alarms.getH() : alarms.getH())  + ":" + (alarms.getM() < 10 ? "0" + alarms.getM()  : alarms.getM()));
        holder.spinner.setSelection(alarms.getWD());
        holder.spinner2.setSelection(alarms.getPos() ? 1 : 0);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Your implementation for item selection
                alarms.setWD(position);
                update_alarm(alarms,pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Your implementation for nothing selected
            }
        });
        holder.spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Your implementation for item selection
                alarms.setPos(position == 1 ? true : false);
                update_alarm(alarms,pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Your implementation for nothing selected
            }
        });

        holder.active.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                alarms.setAct(isChecked);
                update_alarm(alarms,pos);
            }
        });

        holder.time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String[] time = holder.time.getText().toString().split(":");
                    int h = Integer.parseInt(time[0]);
                    int m = Integer.parseInt(time[1]);
                    if(h>= 0 && h<=23 && m>=0 && m<=59){
                        alarms.setH(h);
                        alarms.setM(m);
                        update_alarm(alarms,pos);
                        holder.time.setCursorVisible(false);
                    }else{
                        Toast.makeText(mcontext, R.string.Error_input, Toast.LENGTH_SHORT).show();
                        holder.time.setText((alarms.getH() < 10 ? "0" + alarms.getH() : alarms.getH())  + ":" + (alarms.getM() < 10 ? "0" + alarms.getM()  : alarms.getM()));
                    }


                    return false;
                }
                return true;
            }
        });
        holder.time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                holder.time.setCursorVisible(true);

            }
        });
        holder.time.addTextChangedListener(new TextWatcher() {
            private boolean ignor = true;
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ignor){
                    String str = s.toString();
                    int pos = str.indexOf(":");
                    int sel = holder.time.getSelectionStart();
                    boolean l = false;
                    if(pos == -1) {
                        str = ":";
                        l=true;
                    }

                    if(str.length()>5){
                        str = str.substring(0,5);
                        l=true;
                    }
                    if(pos > 2){
                        if(str.length() != pos+1) {
                            str = str.substring(0, 2) + ":" + str.charAt(2) + str.charAt(4);
                        }else{
                            str = str.substring(0, 2) + ":" + str.charAt(2);
                        }
                        l=true;
                    }
                    if(l){
                        ignor = false;
                        holder.time.setText(str);
                        if(sel == 3) {
                            Selection.setSelection(holder.time.getEditableText(), 4);
                        }else if(sel == 6){
                            Selection.setSelection(holder.time.getEditableText(), 5);
                        }else{
                            Selection.setSelection(holder.time.getEditableText(), sel);
                        }
                    }
            }else{
                ignor = true;
            }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        holder.time.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed by checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace

                    String str = holder.time.getText().toString();
                    int pos = str.indexOf(":");
                    int sel = holder.time.getSelectionStart();
                    int sel_end = holder.time.getSelectionEnd();
                    int f = str.length();
                    if(sel-1 == pos){
                        Selection.setSelection(holder.time.getEditableText(), sel-1);
                    }else if(sel == 0 && sel_end == str.length()) {
                        holder.time.setText(":");
                    }else if(sel !=  sel_end){
                        Selection.setSelection(holder.time.getEditableText(), 0);
                    }
                }
                return false;
            }
        });
    }
    public void update_alarm(Alarms alarms,int position){
        Alarm.object_model.update_one_alarm(alarms,position);
        dataBaseHelper.update_one(Alarm.object_model);
        publish(Alarm.object_model.getAlarmStr_for_esp(), "/sub_ALARM", Alarm.object_model);
    }
    public void add_one(Alarms alarms){
        courseDataArrayList.add(alarms);
        notifyItemInserted(getItemCount()-1);

    };
    public void removeItem(int position) {
        courseDataArrayList.remove(position);
        notifyItemRemoved(position);
    }

//    public void del_one(Alarms alarms,int n){
//        List<Alarms> result = new ArrayList<>();
//        for(Alarms item : courseDataArrayList)
//            if(!deleteMe.equals(item))
//                result.add(item);
//
//
//        courseDataArrayList.de(alarms);
//        List<Alarms>
//
//    };

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder_Alarms extends RecyclerView.ViewHolder {

        private Spinner spinner;
        private Spinner spinner2;
        private TextView time;
        private RMSwitch active;
        public RecyclerViewHolder_Alarms(@NonNull View itemView) {

            super(itemView);
            spinner = itemView.findViewById(R.id.spinner);
            spinner2 = itemView.findViewById(R.id.spinner2);
            time = itemView.findViewById(R.id.time);
            active = itemView.findViewById(R.id.active);
            spinner2.clearAnimation();

            ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(mcontext, R.array.Weekdays,R.layout.spinner_list);
            adapter.setDropDownViewResource(R.layout.spinner_list);
            spinner.setAdapter(adapter);

            ArrayAdapter<CharSequence> adapter2= ArrayAdapter.createFromResource(mcontext, R.array.ON_OFF,R.layout.spinner_list);
            adapter2.setDropDownViewResource(R.layout.spinner_list);
            spinner2.setAdapter(adapter2);

        }
    }


}
