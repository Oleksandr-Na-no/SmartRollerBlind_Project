package com.example.smart_roller_blind;


import static com.example.smart_roller_blind.MainActivity.adapter;
import static com.example.smart_roller_blind.MainActivity.client;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.MainActivity.open2;
import static com.example.smart_roller_blind.MainActivity.recyclerView;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.publish;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    public List<Object_model> getCourseDataArrayList() {
        return courseDataArrayList;
    }

    public void setCourseDataArrayList(List<Object_model> courseDataArrayList) {
        this.courseDataArrayList = courseDataArrayList;
    }

    private List<Object_model> courseDataArrayList;
    private Context mcontext;
    static int pos = 0;
    static boolean ubd = true;

    public RecyclerViewAdapter(List<Object_model> recyclerDataArrayList, Context mcontext) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        Object_model object_model = courseDataArrayList.get(position);
        holder.courseTV.setText(object_model.getName());

        int pos1 = position;

        //get pologrnna/////

        if (open2) {
            object_model.setActive(false);
            dataBaseHelper.update_one(object_model);

            String topicName = object_model.getSup_devise() + "/#";
            try {
                client.subscribe(new MyTopic(topicName, AWSIotQos.QOS1, position));
            } catch (AWSIotException e) {
                e.printStackTrace();
            }

            Log.e("Pub: ",topicName);

            publish(object_model.getisOpen() ? "1" : "0", "/sub_A", object_model);

            if(position >= getItemCount() - 1) {
                open2 = false;
            }
        }
        if (object_model.getActive()) {
            Show_pos(object_model,holder);
        } else {
            holder.button.setBackgroundResource(R.drawable.btn_gray);
            holder.text2.setText(R.string.Offline);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(object_model.getActive()) {
                    object_model.setOpen(!object_model.getisOpen());
                    dataBaseHelper.update_one(object_model);
                    Show_pos(object_model, holder);
                    ubd = false;
                    publish(object_model.getisOpen() ? "1" : "0", "/sub", object_model);
                }else{
                    publish(object_model.getisOpen() ? "1" : "0", "/sub_A", object_model);
                    Toast.makeText(mcontext,R.string.Unavaible, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.courseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = pos1;
                devise_tad(object_model, pos1);
            }
        });
        holder.courseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devise_tad(object_model, pos1);
                pos = pos1;
            }
        });
    }

    private void devise_tad(Object_model object_model, int position3){
        if(!object_model.getActive()){
            publish(object_model.getisOpen() ? "1" : "0", "/sub_A", object_model);
            Toast.makeText(mcontext,R.string.Unavaible, Toast.LENGTH_SHORT).show();
        }
        Put_object(mcontext, Device_Activity.class, object_model);
    }

    private static void Show_pos(Object_model object_model,RecyclerViewHolder holder) {
        if (object_model.getisOpen()) {
            holder.button.setBackgroundResource(R.drawable.btn_1);
            holder.text2.setText(R.string.On);
        } else {
            holder.button.setBackgroundResource(R.drawable.bnt_1_2);
            holder.text2.setText(R.string.Off);
        }
    }

    private class MyTopic extends AWSIotTopic {
        private Object_model object_model2;
        private int position1;

        public MyTopic(String topic, AWSIotQos qos,  int position2) {
            super(topic, qos);
            position1 = position2;
            Log.e("Pesivd: ",String.valueOf(position1));
        }

        @Override
        public void onMessage(AWSIotMessage message) {
            //called when a message is received
            if(ubd) {
                boolean ub = false;
                object_model2 = courseDataArrayList.get(position1);
                String topic1 = message.getTopic();
                topic1 = topic1.split("/")[1];
                if (topic1.equals("pub")) {
                    String str = message.getStringPayload();
                    String[] strmas = str.split("/");
                    object_model2.setPosition(Integer.parseInt(strmas[0]));
                    object_model2.setSpeed_up(Integer.parseInt(strmas[1]));
                    object_model2.setSpeed_down(Integer.parseInt(strmas[2]));
                    object_model2.setLength(Integer.parseInt(strmas[3]));
                    object_model2.setSensor_now(Integer.parseInt(strmas[4]));
                    object_model2.setAuto_on("1".equals(strmas[5]));
                    object_model2.setAuto_threshold(Integer.parseInt(strmas[6]));

                    if ((object_model2.getPosition() == 0) == object_model2.getisOpen()) {
                        object_model2.setOpen(object_model2.getPosition() != 0);
                        ub = true;
                    }
                    if (!object_model2.getActive()) {
                        publish("1", "/sub_A", object_model2);
                        object_model2.setActive(true);
                        ub = true;
                    }
                    dataBaseHelper.update_one(object_model2);

                } else if (topic1.equals("sub")) {
                    String str = message.getStringPayload();
                    if (str.equals("1") != object_model2.getisOpen()) {
                        object_model2.setOpen(str.equals("1"));
                        dataBaseHelper.update_one(object_model2);
                        ub = true;
                    }

                }
                if (ub) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position1);
                        }
                    });
                }
            }

            Log.e("Pesivd: " + position1, object_model2.getSup_devise());
        }
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }
    public void update_ofline(){
        open2 = true;
        notifyDataSetChanged();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView courseTV;
        private ImageView courseIV;
        private View button;
        private TextView text2;
        private View view4;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTV = itemView.findViewById(R.id.idTVCourse);
            courseIV = itemView.findViewById(R.id.idIVcourseIV);
            button = itemView.findViewById(R.id.buttonm);
            text2 = itemView.findViewById(R.id.text2);
            view4 = itemView.findViewById(R.id.view4);

        }
    }

}

