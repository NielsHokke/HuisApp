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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-10-2017.
 */

public class ProMax {

    private static final String TAG = "ProMax";

    private boolean isOn;
    private boolean isHidden;
    ImageView ProMax_IV;
    private int sourceOn;
    private int sourceOff;
    private String offCode;
    private String onCode;

    String url ="http://192.168.178.100/deur_authenticatie/public/promaxcontroller/get";
    RequestQueue queue;

    String subUrl;

    public ProMax(final Context context, RelativeLayout rootView, RequestQueue q, String onCodePM, String offCodePM, int srcOn, int srcOff, boolean on, boolean hidden, int default_x, int default_y){
        ProMax_IV = new ImageView(context);
        isHidden = hidden;
        isOn = on;
        sourceOn = srcOn;
        sourceOff = srcOff;
        queue = q;
        onCode = onCodePM;
        offCode = offCodePM;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.floorView);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.floorView);
        ProMax_IV.setLayoutParams(layoutParams);
        ProMax_IV.setX(default_x);
        ProMax_IV.setY(default_y);
        ProMax_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ProMax_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        rootView.addView(ProMax_IV, 2);

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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?id=" + onCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            if(jObject.getString("success").equals("Code sent successfully")) {
                                isOn = true;
                                setView();
                            }else{
//                                Log.d("ProMax", "Received some kind of error: " + response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        queue.add(stringRequest);
    }

    public void off(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?id=" + offCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("success").equals("Code sent successfully")) {
                                isOn = false;
                                setView();
                            } else {
//                                Log.d("ProMax", "Received some kind of error: " + response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        queue.add(stringRequest);
    }

    public void toggle(){
        if(isOn){
            off();
        }else{
            on();
        }
    }

    void hide(){
        isHidden = true;
        setView();
    }

    void unhide(){
        isHidden = false;
        setView();
    }

    void setView(){
        if(isOn){
            ProMax_IV.setImageResource(sourceOn);
        }else{
            ProMax_IV.setImageResource(sourceOff);
        }

        if(isHidden){
            ProMax_IV.setVisibility(View.INVISIBLE);
        }else{
            ProMax_IV.setVisibility(View.VISIBLE);
        }
    }

}
