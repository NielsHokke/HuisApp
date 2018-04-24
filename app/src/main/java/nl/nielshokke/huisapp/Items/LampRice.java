package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nl.nielshokke.huisapp.Dialogs.ColorLampDialogFragment;
import nl.nielshokke.huisapp.R;

public class LampRice extends Lamp {

    List<Lamp> LampList;

    public LampRice(final Context context, LampGroup group, RelativeLayout rootView, RequestQueue q, String urlName, int srcOn, int srcOff, boolean on, boolean hidden, int default_x, int default_y) {
        super(context, group, rootView, q, urlName, srcOn, srcOff, on, hidden, default_x, default_y);
        url ="http://192.168.178.205/cgi-bin/GrootLightBridge.py";
        LampList = new ArrayList<>();
//        Log.d("LampRice", "LampRice created");
    }

    public void setLamp(String statusString){
        try {
            JSONObject jObject = new JSONObject(statusString);

//            Log.d("LampRice", "LampRice status json: " + jObject);

            if(Float.valueOf(jObject.getString(subUrl)) > 0){
//                Log.d("LampRice", "LampRice " + subUrl + " is on");
                displayOn(subUrl);
//                if(displayOn(subUrl)){
//                    Log.d("LampRice", "LampRice suburl is " + subUrl + " and should be turned on now.");
//                }
            }else{
//                Log.d("LampRice", "LampRice " + subUrl + " is off");
                displayOff(subUrl);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateLamp(String statusString){
        if(statusString == null){
//            Log.d("LampRice", "LampRice status request: " + url  + "?cmd=status");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=status",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.d("LampRice", "LampRice status response: " + response);
                            setLamp(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(stringRequest);
        }else{
            setLamp(statusString);
        }
    }

}
