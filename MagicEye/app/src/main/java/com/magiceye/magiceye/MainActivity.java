package com.magiceye.magiceye;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;
import java.io.FileOutputStream;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    public static ImageView jIV;
    public static boolean frameChanged = false;
    public static Bitmap frame = null;
    public static Context context;
    public static EditText jIP;
    public static String servername;
    private static Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        SharedPreferences sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
        //SharedPreferences.Editor editor = sp.edit();
        String lastIP = sp.getString("Pref_IP", "");
        jIV = (ImageView) findViewById(R.id.xIV);
        jIP = (EditText) findViewById(R.id.xIP);
        jIP.setText(lastIP);
        ToggleButton xB = (ToggleButton) findViewById(R.id.xB);
        ToggleButton xL = (ToggleButton) findViewById(R.id.xL);
        assert xB != null;
        assert xL != null;

        servername = jIP.getText().toString();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (frameChanged) {
                        Log.d("DEBUGjytdktd", "BLAHBLAH");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                jIV.setImageBitmap(frame);
                            }
                        });
                        frameChanged = false;
                    }
                }
            }
        });
        t2.start();


        xB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    servername = jIP.getText().toString();
                    SharedPreferences sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Pref_IP", servername);
                    editor.commit();

                    Intent intent = new Intent(getBaseContext(), NotifyService.class);
                    MainActivity.this.startService(intent);
                }else {
                    Intent intent = new Intent();
                    intent.setAction(NotifyService.ACTION);
                    intent.putExtra("RQS", NotifyService.RQS_STOP_SERVICE);
                    sendBroadcast(intent);
                }
            }
        });

        /*xL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Client();
                t.start();


            }
        });*/

        xL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    client = new Client();
                    client.start();

                }else{
                    client.liveFeed=false;
                    client=null;
                }
            }
        });


    }

    @Override
    protected void onStop() {
        SharedPreferences sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (jIP == null) editor.putString("Pref_IP", servername);
        else editor.putString("Pref_IP", jIP.getText().toString());
        editor.commit();
        super.onStop();
    }
}