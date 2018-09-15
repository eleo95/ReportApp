package com.eleo95.reportapp.myapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.eleo95.reportapp.R;

public class MyApplication extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    getString(R.string.channel_1),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription(getString(R.string.channel_1_descrip));

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    getString(R.string.channel_2),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription(getString(R.string.channel_2_descrip));

            NotificationManager manager = getSystemService(NotificationManager.class);
           if(manager!=null){
               manager.createNotificationChannel(channel1);
               manager.createNotificationChannel(channel2);
           }
        }
    }
}
