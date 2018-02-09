package nl.nielshokke.huisapp.Items;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    private ImageView Lamp_IV;
    private int sourceOn;
    private int sourceOff;

    String url ="http://192.168.178.202/cgi-bin/GrootLightBridge.py";
    RequestQueue queue;

    String subUrl;
    private LampGroup parentGroup;

    public Lamp(Context context, LampGroup group, RelativeLayout rootView, RequestQueue q, String urlName,  int srcOn, int srcOff, boolean on, boolean hidden){
        Lamp_IV = new ImageView(context);
        isHidden = hidden;
        isOn = on;
        sourceOn = srcOn;
        sourceOff = srcOff;
        queue = q;
        subUrl = urlName;
        parentGroup = group;

        Lamp_IV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Lamp_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rootView.addView(Lamp_IV, 2);

        setView();
    }

    protected boolean isOn() {
        return isOn;
    }

    protected void setOn(boolean on) {
        isOn = on;
    }

    public void on(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_aan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isOn = true;
                        setView();
                        if(response.contains("aan -")){
                            updateGroup();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(stringRequest);
    }

    public void off(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_uit",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isOn = false;
                        setView();
                        if(response.contains("uit -")){
                            updateGroup();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
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

    void updateGroup(){
        parentGroup.updateGroup();
    }

    void hide(){
        isHidden = true;
        setView();
    }

    void unhide(){
        isHidden = false;
        setView();
    }

    void displayOn(String name){
        if(name.equals("") || name.equals(subUrl)){
            isOn = true;
            setView();
        }
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
