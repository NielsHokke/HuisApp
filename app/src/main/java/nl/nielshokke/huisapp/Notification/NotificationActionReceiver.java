package nl.nielshokke.huisapp.Notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Nelis on 28-10-2017.
 */

public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationAction";
    private static final String OPEN_DOOR_TAG = "Sesam, open u";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (OPEN_DOOR_TAG.equals(intent.getAction())){
            Log.d(TAG, "opening door");
            RequestQueue queue = Volley.newRequestQueue(context);

            final String android_id = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.178.200/cgi-bin/openDoor.py", new Response.Listener<String>(){
                @Override
                public void onResponse(String s){}
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError){}
            }){
                @Override
                protected Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("key", android_id);
                    return params;
                }
            };
            queue.add(stringRequest);

            //close notification
            NotificationManager mNotifyMgr=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(1);
        }
    }
}
