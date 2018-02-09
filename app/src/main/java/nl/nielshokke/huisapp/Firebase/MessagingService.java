package nl.nielshokke.huisapp.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import nl.nielshokke.huisapp.Notification.FrontdoorNotification;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 8-2-2018.
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //get data outof json object
            JSONObject mainObject = new JSONObject(remoteMessage.getData());
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.voordeur_test_foto);
            String time = "";
            Boolean test = false;

            try {
                bm = getBitmapFromURL(mainObject.getString("image_url"));
                time = mainObject.getString("time");
                test = mainObject.getBoolean("test");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            FrontdoorNotification notification = new FrontdoorNotification(getBaseContext());
            notification.show(bm,time, test);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
