package nl.nielshokke.huisapp.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

import nl.nielshokke.huisapp.MainActivity;
import nl.nielshokke.huisapp.R;

import static android.app.Notification.PRIORITY_MAX;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Nelis on 26-10-2017.
 */

public class FrontdoorNotification {

    private Context mContext;
    private static final String FRONTDOOR_NOTIFICATION_SETTINGS = "frontdoorNotificationSettings";
    private static final String OPEN_CAMERA_TAG = "frontdooroOpenCamera";
    private static final String OPEN_DOOR_TAG = "Sesam, open u";
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private static final String TAG = "notification";

    private boolean isOnline = false;
    private Bitmap Rbm = null;
    private String Rtime = null;
    private Boolean Ristest = null;

    public FrontdoorNotification(Context context){
        mContext = context;
    }

    public void show(Bitmap bm, String time, Boolean istest, Boolean update){
        Rbm = bm;
        Rtime = time;
        Ristest = istest;
        // Sets an ID for the notification
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        int notifiSetting = Integer.parseInt(sharedPref.getString("pref_notification_type", "1"));
        Log.d(TAG, "nofity setting: " + notifiSetting);
        if(notifiSetting == 3){
            return;
        }else if(notifiSetting == 2 && !update){
            checkIfOnline();
            return;
        }

        long elapsedtime = 0;
        if(time.equals("")){
            time="00:00:00";
        }else{
            //parse time to seconds
            String[] units = time.split("-");
            int hours = Integer.parseInt(units[0]);
            int minutes = Integer.parseInt(units[1]);
            int seconds = Integer.parseInt(units[2]);
            int duration = 3600 * hours + 60 * minutes + seconds;

            //calculate millis since midnight
            Calendar rightNow = Calendar.getInstance();
            long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
            long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
            elapsedtime = (duration * 1000) - sinceMidnight;
        }

        //intent for when notification is clicked
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.setAction(OPEN_CAMERA_TAG);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //intent for when open_door button is clicked
        Intent actionIntent = new Intent(mContext, NotificationActionReceiver.class);
        actionIntent.setAction(OPEN_DOOR_TAG);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(mContext, 0, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //loading remote views
        RemoteViews bigremoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_frontdoor);//R.layout.custom_notification

        if(isOnline){
            bigremoteViews.setBoolean(R.id.openButton, "setEnabled", true);
        }

        if(bm == null){
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.voordeur_test_foto);
        }
        bigremoteViews.setImageViewBitmap(R.id.notificationImage, bm);
        bigremoteViews.setImageViewResource(R.id.imageViewClock, R.drawable.clock);
        bigremoteViews.setImageViewResource(R.id.imageViewWaiting, R.drawable.timer);
        bigremoteViews.setOnClickPendingIntent(R.id.openButton, actionPendingIntent);
        bigremoteViews.setTextViewText(R.id.chronometerTime, time.replace("-",":"));
        bigremoteViews.setChronometer(R.id.chronometerTimer, elapsedtime + SystemClock.elapsedRealtime(), null, true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ding Dong frontdoor", NotificationManager.IMPORTANCE_DEFAULT);


            // Configure the notification channel.
            notificationChannel.setDescription("Channel description 1");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.purple);
            notificationChannel.enableVibration(false);

            Log.d(TAG, "pref_vibrate: " + sharedPref.getBoolean("pref_vibrate", true));
            Log.d(TAG, "pref_sound: " + sharedPref.getBoolean("pref_sound", true));


            if (sharedPref.getBoolean("pref_vibrate", true)) {
                // play vibration
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200}, -1));
            }

            if (sharedPref.getBoolean("pref_sound", true)) {
                // play sound
                MediaPlayer mp = MediaPlayer.create(mContext, R.raw.frontdoor_notification_sound);
                mp.start();
            }


            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                    .setStyle(new Notification.DecoratedCustomViewStyle())
                    .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                    .setCustomBigContentView(bigremoteViews)
                    .setContentText("someone's at the door")
                    .setSmallIcon(R.drawable.ic_door_icon)
                    .setAutoCancel(true)
                    .setUsesChronometer(true)
                    .setContentIntent(resultPendingIntent);

            if(istest){
                builder.setContentTitle("Ding Dong. This is a Test");
            }else{
                builder.setContentTitle("Ding Dong");
            }


            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }else{
            NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(mContext)
                    .setStyle(new android.support.v7.app.NotificationCompat.DecoratedCustomViewStyle())
                    .setColor(mContext.getResources().getColor(R.color.colorPrimary))
                    .setCustomBigContentView(bigremoteViews)
                    .setContentText("someone's at the door")
                    .setSmallIcon(R.drawable.ic_door_icon)
                    .setAutoCancel(true)
                    .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(resultPendingIntent);

            if(istest){
                builder.setContentTitle("Ding Dong. This is a Test");
            }else{
                builder.setContentTitle("Ding Dong");
            }

            if(sharedPref.getBoolean("pref_vibrate", true)){
                builder.setVibrate(new long[] { 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200 });
            }
            if(sharedPref.getBoolean("pref_sound", true)){
                Uri sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.frontdoor_notification_sound);
                builder.setSound(sound);
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        if(!isOnline){
            checkIfOnline();
        }
    }
    public void checkIfOnline(){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.200/cgi-bin/isOnline.py",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isOnline = true;
                        show(Rbm, Rtime, Ristest, true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isOnline = false;
            }
        });
        queue.add(stringRequest);
    }
}