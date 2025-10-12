package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.MainActivity.adapter;
import static com.example.smart_roller_blind.MainActivity.client;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.MainActivity.recyclerView;
import static com.example.smart_roller_blind.Settings_Activity.Theme;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

public class Methods {
    public static void ubdate_list(){
//        if(client.getConnectionStatus().name() == "DISCONNECTED"){
            adapter.update_ofline();
//        }
    }
    public static void publish(String payload,String topic, Object_model object_model) {
        AWSIotConnectionStatus a = client.getConnectionStatus();
        if(a.name() == "DISCONNECTED"){
            Log.e("IIIIIIIIIIII", "DISCONNECTED");
            //rty first disconect
//            Aws_login();
        }else {
            if (internetIsConnected()) {
                Send_Task myTask = new Send_Task();
                myTask.execute(new Publish(topic,payload,object_model));
            } else {
                Log.e("IIIIIIIIIIII", "NO INTIRNET");
            }
        }
    }

    private static class Send_Task extends AsyncTask<Publish, String, String> {
        @Override
        protected String doInBackground(Publish... publish0) {
            Publish publish =  publish0[0];
            Object_model object_model = publish.getObject_model();
            String topic = object_model.getSup_devise() + publish.getTopic();
            MyMessage message = new MyMessage(topic, AWSIotQos.QOS1, publish.getPayload());
            long timeout = 2000;// milliseconds
            try {
                client.publish(message, timeout);//client.publish(topic, AWSIotQos.QOS1, payload);
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
            return "Exexut";
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }

    public static class MyMessage extends AWSIotMessage {
        public MyMessage(String topic, AWSIotQos qos, String payload) {
            super(topic, qos, payload);
        }
        @Override
        public void onSuccess() {
            // called when message publishing succeeded
            Log.e("IIIIIIIIIIII", "Sended"+" "+payload+" "+topic);
        }

        @Override
        public void onFailure() {
            // called when message publishing failed
            Log.e("IIIIIIIIIIII", "Error_send");
        }

        @Override
        public void onTimeout() {
            // called when message publishing timed out
            Log.e("IIIIIIIIIIII", "Timed_out_send");
        }
    }
    public static boolean mobile_data_check(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return  activeNetwork != null && activeNetwork.isConnected();

    }

    public static boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public static void  set_theme(Context context) {
        switch(Theme) {
            case 1:
                context.setTheme(R.style.Theme1);
                break;
            case 2:
                context.setTheme(R.style.Theme2);
                break;
            case 3:
                context.setTheme(R.style.Theme3);
                break;
            case 4:
                context.setTheme(R.style.Theme4);
                break;
            case 5:
                context.setTheme(R.style.Theme5);
                break;
            case 6:
                context.setTheme(R.style.Theme6);
                break;
            case 7:
                context.setTheme(R.style.Theme7);
                break;
            case 8:
                context.setTheme(R.style.Theme8);
                break;
        }
    }
    public static void Put_object(Context mcontext, Class class_, Object_model object_model){
        Intent intent = new Intent(mcontext,class_);
        intent.putExtra("id",object_model.getId());
        intent.putExtra("name",object_model.getName());
        intent.putExtra("isopen",object_model.getisOpen());
        intent.putExtra("position",object_model.getPosition());
        intent.putExtra("sup_devise",object_model.getSup_devise());
        intent.putExtra("speed_up",object_model.getSpeed_up());
        intent.putExtra("speed_down",object_model.getSpeed_down());
        intent.putExtra("active",object_model.getActive());
        intent.putExtra("length",object_model.getLength());
        intent.putExtra("active_check",object_model.getActive_check());
        intent.putExtra("image",object_model.getImage());
        intent.putExtra("auto_on",object_model.getAuto_on());
        intent.putExtra("auto_threshold",object_model.getAuto_threshold());
        intent.putExtra("sensor_now",object_model.getSensor_now());
        intent.putExtra("alarm",object_model.getAlarmStr());
        mcontext.startActivity(intent);
    }

    public static Object_model Get_object(Intent intent){
        return new Object_model(intent.getStringExtra("name"),
                intent.getBooleanExtra("isopen",true),
                intent.getIntExtra("position",-1),
                intent.getIntExtra("id",-1),
                intent.getStringExtra("sup_devise"),
                intent.getIntExtra("speed_up",-1),
                intent.getIntExtra("speed_down",-1),
                intent.getBooleanExtra("active",true),
                intent.getIntExtra("length",100),
                intent.getBooleanExtra("active_check",true),
                intent.getStringExtra("image"),
                intent.getBooleanExtra("auto_on",true),
                intent.getIntExtra("auto_threshold",500),
                intent.getIntExtra("sensor_now",500),
                intent.getStringExtra("alarm")
        );
    }

//    public static void Aws_login() {
//        try {
//            client.connect();
//            Log.e("IIIIIIIIIIII","Conect");
//        } catch (AWSIotException e) {
//            e.printStackTrace();
//            Log.e("IIIIIIIIIIII","Error Conect");
//        }
//    }

//    public static class Async_Task_Send extends AsyncTask<Object_model, String, String> {
//        @Override
//        protected String doInBackground(Object_model... strings) {
//            Object_model object_model1 = strings[0];
//            publish(object_model1.getisOpen() ? "1" : "0", "/sub", object_model1);
//
//            return "Exexut";
//        }
//    }
    ////////////
//    public static void publish1(String payload,String topic, Object_model object_model,Context context){
//        Async_Task_Send async_Task_Send=new Async_Task_Send();
//        async_Task_Send.execute(payload,object_model.getSup_devise()+topic);
//        }
//    public static class Async_Task_Send extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            String topic = strings[0];
//            String payload = strings[0];
//            MyMessage message = new MyMessage(topic, AWSIotQos.QOS1, payload);
//            long timeout = 3000;// milliseconds
//            try {
//                client.publish(message, timeout);
//            } catch (AWSIotException e) {
//                e.printStackTrace();
//            }
//            return ;
//        }
//
//        //        @Override
////        protected void onProgressUpdate(String... values) {
////
////        }
//        @Override
//        protected void onPostExecute(String result) {
//
//        }
//    }
}
