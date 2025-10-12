package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.Add_device.PASSWORD;
import static com.example.smart_roller_blind.Add_device.USSD;
import static com.example.smart_roller_blind.MainActivity.adapter;
import static com.example.smart_roller_blind.MainActivity.client;
import static com.example.smart_roller_blind.MainActivity.clientId;
import static com.example.smart_roller_blind.MainActivity.dataBaseHelper;
import static com.example.smart_roller_blind.MainActivity.recyclerView;
import static com.example.smart_roller_blind.MainActivity.status;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.internetIsConnected;
import static com.example.smart_roller_blind.Methods.mobile_data_check;
import static com.example.smart_roller_blind.Methods.publish;
import static com.example.smart_roller_blind.Methods.set_theme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.AWSIotTopic;

import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Loading extends AppCompatActivity {
    View view25;
    View view28;
    View view31;
    View view29;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    WebView myWebView;
    ProgressBar progressBar;
    ProgressBar circleBar;

    private Object_model object_model;
    String sub = "";
    int send = 0;
    boolean nex = false;
    int n = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        set_theme(this);//////theme//////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        view25 = (View)findViewById(R.id.view25);
        view28 = (View)findViewById(R.id.view28);
        view31 = (View)findViewById(R.id.view31);
        view29 = (View)findViewById(R.id.view29);
        image1 = (ImageView)findViewById(R.id.idIVcourseIV);
        image2 = (ImageView)findViewById(R.id.idIVcourseIV4);
        image3 = (ImageView)findViewById(R.id.Image3);
        image4 = (ImageView)findViewById(R.id.Image4);
        image5 = (ImageView)findViewById(R.id.Image6);
        myWebView = (WebView)findViewById(R.id.webview);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        circleBar = (ProgressBar)findViewById(R.id.progressBar);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                myWebView.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                Log.d("HTML", html);
                                //when page loaded
                                String input = html;
                                boolean isFound = input.indexOf("Sended") != -1 ? true : false; //true
                                if (isFound) {
                                    n = 4;
                                    view31.setBackgroundResource(R.drawable.blok);
                                    image1.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
                                    image3.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyTask3 myTask = new MyTask3();
                                            myTask.execute();
                                        }
                                    }, 3000);
                                }else{
                                    image3.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                                    image1.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                                }
                            }
                        });
            }
        });

    }

    public void ClickWifi(View view) {
        if(n<4) {
            try {
                client.disconnect(1000,false);
            } catch (AWSIotException | AWSIotTimeoutException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {// if build version is less than Q try the old traditional method
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    image2.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 30);
                } else {
                    wifiManager.setWifiEnabled(true);

                    String networkSSID = "ESP82";
                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration i : list) {
                        if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();
                            break;
                        }
                    }
                    MyTask2 myTask = new MyTask2();
                    myTask.execute();
                }
            } else {// if it is Android Q and above go for the newer way    NOTE: You can also use this code for less than android Q also
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivityForResult(panelIntent, 1);
                // Do something after 5s = 5000ms
                MyTask4 myTask = new MyTask4();
                myTask.execute();

            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view25.setBackgroundResource(R.drawable.blok);
                    if (n == 1) {
                        n = 2;
                    }
                }
            }, 5000);

        }
    }
    public void ClickMobileData(View view) {
        if(n>1 & n<=3) {
            Log.e("mobile_data_check", String.valueOf(mobile_data_check(this)));

            if (mobile_data_check(this)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    MyTask myTask = new MyTask();
                    myTask.execute();
                    try {
                        String[] cmds = {"svc data disable"};
                        Process p = Runtime.getRuntime().exec("su");
                        DataOutputStream os = new DataOutputStream(p.getOutputStream());
                        for (String tmpCmd : cmds) {
                            os.writeBytes(tmpCmd + "\n");
                        }
                        os.writeBytes("exit\n");
                        os.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                if(n < 3) {
                    n = 3;
                }
                view28.setBackgroundResource(R.drawable.blok);
                image2.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

    }

    public void ClickSending(View view) {
        if(n==3) {
            if (mobile_data_check(Loading.this)) {
                image2.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (internetIsConnected()) {
                image1.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                image3.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String strDate = sdf.format(c.getTime());
                sub = clientId + "_" + strDate;
                object_model = new Object_model("No Name", true, 128, 0, sub, 200, 300, true, 1000, true, "image", true, 500, 500, "1,21,35,1,0");
                String Send = "http://192.168.4.1/get?data=" + USSD + "/i/" + PASSWORD + "/s/" + sub;
                myWebView.loadUrl(Send);
            }
        }
    }
    boolean wimo = true;
    public void ClickConect(View view) {
        if(n==4) {
            if(internetIsConnected()){
                n = 5;
                MyTask5 myTask = new MyTask5();
                myTask.execute();
            }else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                } else {
                    if (wimo) {
                        Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                        startActivityForResult(panelIntent, 1);
                        wimo = false;
                    } else {
                        Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        startActivity(intent);
                        wimo = true;
                    }
                }
            }
        }

    }

    public void ConnectToDevise(){
        circleBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(7);
        view29.setBackgroundResource(R.drawable.blok);
        image4.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);

        String topicName = object_model.getSup_devise() + "/pub";
        MyTopic topic = new MyTopic(topicName, AWSIotQos.QOS0);

        try {
            client.subscribe(topic);
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
        publish(object_model.getisOpen() ? "1" : "0", "/sub_A", object_model);
        send = 0;
        final Handler handler = new Handler();
        handler.postDelayed(new runnable(), 7000);
    }

    public void Clic_p(View view) {
        startActivity(new Intent(Loading.this, Add_device.class));
    }

    private class runnable implements Runnable {
        @Override
        public void run() {
            // Do something after 2s = 2000ms
            if(send < 4) {
                send++;
                progressBar.setProgress(7-send);
                publish("1", "/sub_A", object_model);
                final Handler handler = new Handler();
                handler.postDelayed(new runnable(), 4000*(send+3));
            }else{
                if(send<8){
                    image5.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                    progressBar.setProgress(0);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Loading.this, Add_device.class));
                        }
                    }, 1000);
                }else{
                    Put_object(Loading.this, Name_add_Activity.class, object_model);
                }
            }
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            while(mobile_data_check(Loading.this)){
            }
            return "Exexut";
        }
            @Override
            protected void onPostExecute(String result) {
            final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(n<3) {
                                n = 3;
                            }
                            view28.setBackgroundResource(R.drawable.blok);
                            image2.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
                        }
                    }, 2000);

            }
    }
    private class MyTask2 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            String ssid = "";
            while(ssid != "ESP82"){
                WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                    ssid = wifiInfo.getSSID();
                    Log.e("Internet", ssid);
                }
            }
            return "Exexut";
        }
        @Override
        protected void onPostExecute(String result) {
            image1.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
    private class MyTask3 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            while(!internetIsConnected()){
            }
            return "Exexut";
        }
        @Override
        protected void onPostExecute(String result) {
            n = 5;
            MyTask5 myTask = new MyTask5();
            myTask.execute();
        }
    }

    private class MyTask5 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                client.connect(5000,true);
            } catch (AWSIotException | AWSIotTimeoutException e) {
                e.printStackTrace();
            }
            while(client.getConnectionStatus().name() == "DISCONNECTED"){
                if(!internetIsConnected()){
                    image5.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
            return "Exexut";
        }
        @Override
        protected void onPostExecute(String result) {
            ConnectToDevise();
        }
    }
    private class MyTask4 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            while(internetIsConnected()){
            }
            return "Exexut";
        }
        @Override
        protected void onPostExecute(String result) {
            if(n==1) {
                n = 2;
            }
            view25.setBackgroundResource(R.drawable.blok);
            image1.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private class MyTopic extends AWSIotTopic {
        public MyTopic(String topic, AWSIotQos qos) {
            super(topic, qos);
        }
        @Override
        public void onMessage(AWSIotMessage message) {
//             called when a message is received
            publish("1", "/sub_A", object_model);
            send = 10;
            progressBar.setProgress(10);
            image5.setColorFilter(ContextCompat.getColor(Loading.this, R.color.Green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}