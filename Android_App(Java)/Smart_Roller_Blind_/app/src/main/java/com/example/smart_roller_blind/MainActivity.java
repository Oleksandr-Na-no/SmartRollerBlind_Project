package com.example.smart_roller_blind;

import static com.example.smart_roller_blind.Add_device.PASSWORD;
import static com.example.smart_roller_blind.Add_device.USSD;
import static com.example.smart_roller_blind.Methods.Put_object;
import static com.example.smart_roller_blind.Methods.internetIsConnected;
import static com.example.smart_roller_blind.Methods.publish;
import static com.example.smart_roller_blind.Methods.set_theme;
import static com.example.smart_roller_blind.Methods.ubdate_list;
import static com.example.smart_roller_blind.Settings_Activity.Theme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity{

    public static boolean status = true;
    public static RecyclerView recyclerView;
    public static AWSIotMqttClient client;

    String clientEndpoint = "[hiden].iot.us-east-1.amazonaws.com";   // use value returned by describe-endpoint --endpoint-type "iot:Data-ATS"
    String awsAccessKeyId = "[hiden]";
    String awsSecretAccessKey = "[hiden]";
    public static String clientId;

    public static DataBaseHelper dataBaseHelper;
    public static boolean open = true;
    public static boolean open2 = true;
    public static SharedPreferences settings;

    public static RecyclerViewAdapter adapter;

    private Uri uri;

//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        adapter.notifyDataSetChanged();
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(getIntent());
//        overridePendingTransition(0, 0);
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        ubdate_list();
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(open){//first time screen open

            GetSaveVariables();//get save variables
            set_theme(this);//////theme//////
            setContentView(R.layout.activity_main);
            recyclerView=findViewById(R.id.idCourseRV);

            if (settings.getBoolean("my_first_time", true)) {//FIRS TIME
                //the app is being launched for first time, do something
                first_time(settings);
            }

            client = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey, null);
            dataBaseHelper = new DataBaseHelper(MainActivity.this);

            if(internetIsConnected()){
                try {
                    client.connect(4000,true);
                    Log.e("IIIIIIIIIIII","Conect");
                } catch (AWSIotException | AWSIotTimeoutException e) {
                    e.printStackTrace();
                    Log.e("IIIIIIIIIIII","Error Conect");
                }
            }else{
                new Handler().postDelayed(new runnable(), 1000);
                try {
                    client.connect(4000,false);
                } catch (AWSIotException e) {
                    e.printStackTrace();
                } catch (AWSIotTimeoutException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, R.string.No_Internet, Toast.LENGTH_SHORT).show();
            }


//            AsyncTaskConect async_Task_Send = new AsyncTaskConect();
//            async_Task_Send.execute();
            setLocale(this,Locale.getDefault().getLanguage());
            open = false;
        }else{
            set_theme(this);//////theme//////
            setContentView(R.layout.activity_main);
            recyclerView=findViewById(R.id.idCourseRV);
        }


        Set_adapter();
//        .setOnLongClickListener(new View.OnLongClickListener() {
//          public boolean onLongClick(View arg0) {
//              Toast.makeText(getApplicationContext(), "Long Clicked ",Toast.LENGTH_SHORT).show();
//
//              return true;    // <- set to true
//          }
//        });
    }

    private void GetSaveVariables() {
        settings = getSharedPreferences("MyPrefsFile", 0);
        clientId = settings.getString("clientId", clientId);
        Theme = settings.getInt("Theme", Theme);
        USSD = settings.getString("USSD", USSD);
        PASSWORD = settings.getString("PASSWORD", PASSWORD);
    }

    private void first_time(SharedPreferences settings) {
        Log.e("Comments", "First time");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(c.getTime());
        //Add phone name to ID
//        String bm = Build.MODEL;
//        bm = bm.replaceAll("\\s+","_");
        clientId = "ID"+strDate + "I";
        // first time task
        Log.e("ClientId","ClientId : " + clientId);
        settings.edit().putString("clientId", clientId).commit();
        // record the fact that the app has been started at least once
        settings.edit().putBoolean("my_first_time", false).commit();
    }

    private void Set_adapter() {
        List<Object_model> everyone = dataBaseHelper.getEveryone();
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);// in this method '2' represents number of columns to be displayed in grid view.
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(everyone,this);
        recyclerView.setAdapter(adapter);//set adapter to recycler view.
    }

    public void Clic1(View view) {
        startActivity(new Intent(MainActivity.this,Add_device.class));

    }

    public void Clic2(View view) {
        startActivity(new Intent(MainActivity.this, Settings_Activity.class));
    }

    public void Clictest(View view) {
        startActivity(new Intent(MainActivity.this, Alarm.class));
    }


    private class runnable implements Runnable {
        @Override
        public void run() {
            // Do something after 2s = 2000ms
            AWSIotConnectionStatus a = client.getConnectionStatus();
            if(a.name() == "DISCONNECTED") {
                new Handler().postDelayed(new runnable(), 1000);
            }else{
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeChanged(0,adapter.getItemCount());
                    }
                });
            }

        }
    }
//    private class AsyncTaskConect extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            while(!internetIsConnected()){
//
//            }
//            return "";
//        }
//        @Override
//        protected void onPostExecute(String bitmap) {
//            Toast.makeText(MainActivity.this, "Conected", Toast.LENGTH_SHORT).show();
//        }
//    }

    //localisation
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void link(){
        uri = getIntent().getData();

        // checking if the uri is null or not.
        if (uri != null) {

            // if the uri is not null then we are getting
            // the path segments and storing it in list.
            List<String> parameters = uri.getPathSegments();

            // after that we are extracting string
            // from that parameters.
            String param = parameters.get(parameters.size() - 1);


        }
    }

}
