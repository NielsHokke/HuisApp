package nl.nielshokke.huisapp.Items;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-10-2017.
 */

public class Lamp {

    private static final String TAG = "Lamp";

    private boolean isOn;
    private boolean isHidden;
    ImageView Lamp_IV;
    private int sourceOn;
    private int sourceOff;

    String url ="http://192.168.178.202/cgi-bin/GrootLightBridge.py";
    RequestQueue queue;

    String subUrl;
    private LampGroup parentGroup;

    public Lamp(final Context context, LampGroup group, RelativeLayout rootView, RequestQueue q, String urlName, int srcOn, int srcOff, boolean on, boolean hidden, int default_x, int default_y){
        Lamp_IV = new ImageView(context);
        isHidden = hidden;
        isOn = on;
        sourceOn = srcOn;
        sourceOff = srcOff;
        queue = q;
        subUrl = urlName;
        parentGroup = group;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.floorView);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.floorView);
        Lamp_IV.setLayoutParams(layoutParams);
        Lamp_IV.setX(default_x);
        Lamp_IV.setY(default_y);
        Lamp_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Lamp_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        rootView.addView(Lamp_IV, 2);

        setView();
    }

    protected boolean isOn() {
        return isOn;
    }

    protected void setOn(boolean on) {
        isOn = on;
        setView();
    }

    public void on(){
//        Log.d("URL Sent", url  + "?cmd=" + subUrl + "_aan");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_aan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isOn = true;
                        if(parentGroup != null) {
                            updateGroup(response);
                        }else{
                            setView();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void off(){
//        Log.d("URL Sent", url  + "?cmd=" + subUrl + "_uit");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_uit",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isOn = false;
                        if(parentGroup != null) {
                            updateGroup(response);
                        }else{
                            setView();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void toggle(){
        if(isOn){
            off();
        }else{
            on();
        }
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_toggle",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        isOn = !isOn;
//                        setView();
//                        Log.d(TAG, "response: " +response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {}
//        });
//        queue.add(stringRequest);
    }

    void updateGroup(String statusString){
        parentGroup.updateGroup(statusString);
    }

    void hide(){
        isHidden = true;
        setView();
    }

    void unhide(){
        isHidden = false;
        setView();
    }

    boolean displayOn(String name){
        if(name.equals("") || name.equals(subUrl)){
            isOn = true;
            setView();
            return true;
        }
        return false;
    }

    void displayOff(String name){
        if(name.equals("") || name.equals(subUrl)) {
            isOn = false;
            setView();
        }
    }

    void setView(){
        if(isOn){
            Lamp_IV.setImageResource(sourceOn);
        }else{
            Lamp_IV.setImageResource(sourceOff);
        }

        if(isHidden){
            Lamp_IV.setVisibility(View.INVISIBLE);
        }else{
            Lamp_IV.setVisibility(View.VISIBLE);
        }
    }

}
