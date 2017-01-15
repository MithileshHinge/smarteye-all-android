package com.magiceye.magiceye;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by mithileshhinge on 30/11/16.
 */
public class NotifyService extends Service {

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;

    NotifyServiceReceiver notifyServiceReceiver;

    private static final int MY_NOTIFICATION_ID = 1;
    private static final int MY_WARNING_ID1 = 2;
    private static final int MY_WARNING_ID2 = 3;
    //public static String servername;
    private NotificationManager notificationManager;

    private Thread t;

    @Override
    public void onCreate() {
        notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);

        //Send notification

        Context context = getApplicationContext();
        String notifTitle = "Someone's at your door!";
        String notifText = "Tap for live feed.";

        Context warning_context1 = getApplicationContext();
        String warnTitle1 = "Something's happening at your door";
        String warnText1 = "Alert level 1";

        Context warning_context2 = getApplicationContext();
        String warnTitle2 = "Suspicious activity";
        String warnText2 = "Alert level 2";

        final NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_notif).setContentTitle(notifTitle).setContentText(notifText).setAutoCancel(true);
        final NotificationCompat.Builder warnBuilder1 = new NotificationCompat.Builder(warning_context1).setSmallIcon(R.drawable.ic_warn).setContentTitle(warnTitle1).setContentText(warnText1).setAutoCancel(true);
        final NotificationCompat.Builder warnBuilder2 = new NotificationCompat.Builder(warning_context2).setSmallIcon(R.drawable.ic_warn).setContentTitle(warnTitle2).setContentText(warnText2).setAutoCancel(true);

        notifBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        warnBuilder1.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        warnBuilder2.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));


        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(myIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notifBuilder.setContentIntent(pendingIntent);
        warnBuilder1.setContentIntent(pendingIntent);
        warnBuilder2.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Toast.makeText(this, "Notification service started", Toast.LENGTH_LONG).show();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        final String servername = sp.getString("Pref_IP", "0");
        Log.d("servername", servername);
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Socket client = new Socket(servername, 6667);
                        Log.d("CONNECTED", "to " + servername);
                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();
                        int p =in.read();
                        System.out.println(p);
                        //Log.d("BYTEREAD", "1");
                        if (p == 1) {
                            notificationManager.notify(MY_NOTIFICATION_ID, notifBuilder.build());
                        }

                        if (p == 2) {
                            notificationManager.notify(MY_WARNING_ID1, warnBuilder1.build());
                        }
                        if (p == 3) {
                            notificationManager.notify(MY_WARNING_ID2, warnBuilder2.build());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("DESTROYEDDD!", "HAHAHA!");
        Toast.makeText(NotifyService.this, "Notification service stopped", Toast.LENGTH_SHORT).show();
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotifyServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int rqs = intent.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE){
                stopSelf();
            }
        }
    }
}
