package com.darkweb.genesissearchengine.pluginManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.darkweb.genesissearchengine.appManager.home_activity.homeModel;
import com.example.myapplication.R;

public class localNotification
{
    @SuppressLint("StaticFieldLeak")

    /*Private Variables*/
    private AppCompatActivity app_context;
    private eventObserver.eventListener event;

    /*Initializations*/

    localNotification(AppCompatActivity app_context, eventObserver.eventListener event){
        this.app_context = app_context;
        this.event = event;
        //mContext = homeModel.getInstance().getAppContext();
    }

    private Context mContext;
    private NotificationManager mNotificationManager;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message)
    {
        //Intent resultIntent = new Intent(mContext , homeModel.getInstance().getHomeInstance().getClass());
        //resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /*PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 , resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.xml.ic_icon_download_notification);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);*/

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            //mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;

        int oneTimeID = (int) SystemClock.uptimeMillis();
        //mNotificationManager.notify(oneTimeID, mBuilder.build());

        //mNotificationManager.notify(oneTimeID /* Request Code */, mBuilder.build());

        new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(5000);
                    mNotificationManager.cancel(oneTimeID);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
