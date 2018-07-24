package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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

/**
 * Created by Nelis on 20-10-2017.
 */

public class LampGroup extends Lamp {

    private static final String TAG = "LampGroup";

    List<Lamp> LampList;
    boolean isInGroupMode;
    boolean isGroupOn;
    private boolean isHidden;


    public LampGroup(final Context context, RelativeLayout rootView, RequestQueue q, String urlName, int srcOn, int srcOff, boolean on, boolean hidden, boolean groupMode, int default_x, int default_y){
        super(context, null, rootView, q, urlName, srcOn, srcOff, on, hidden, default_x, default_y);
        LampList = new ArrayList<>();
        isInGroupMode = groupMode;
        isGroupOn = on;

        if(isHidden){
            hide();
        }else{
            unhide();
        }

        Lamp_IV.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                toggleGroupmode();
                return true;
            }
        });
    }

    public void addLamp(Lamp lamp){
        LampList.add(lamp);
    }

    @Override
    public void toggle(){

        Log.d(TAG,"toggle isInGroupMode:" + isInGroupMode);

        if(isInGroupMode){
            if(isOn()){
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_uit",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                updateGroup(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
                queue.add(stringRequest);
            }else{
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_aan",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                updateGroup(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
                queue.add(stringRequest);
            }
        }else{
            super.toggle();
        }
    }

    public void toggleGroupmode(){
        isInGroupMode = !isInGroupMode;
        setGroupView();
    }

    public void setGroupView(){
        if(isInGroupMode){
            for(Lamp lamp : LampList){
                lamp.hide();
            }
            subUrl = "all";
        }else{
            for(Lamp lamp : LampList){
                lamp.unhide();
            }
            subUrl = "midden";

        }
        //updateGroup(null);
        setView();
    }

    @Override
    void setView(){

        Log.d(TAG,"setView isON:" + isOn() + ", isGroupOn:" + isGroupOn + ", isInGroupMode:" + isInGroupMode);

        if(isOn() || (isGroupOn && isInGroupMode)){
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

    public void setGroup(String statusString){
        try {
            JSONObject jObject = new JSONObject(statusString);

            isGroupOn = false;


            if(Float.valueOf(jObject.getString("Rechts")) > 0){
                for(Lamp lamp : LampList){
                    lamp.displayOn("rechts");
                }
                isGroupOn = true;
            }else{
                for(Lamp lamp : LampList){
                    lamp.displayOff("rechts");
                }
            }

            if(Float.valueOf(jObject.getString("Midden")) > 0){
                setOn(true);
                isGroupOn = true;
            }else{
                setOn(false);
            }

            if(Float.valueOf(jObject.getString("LinksOnder")) > 0){
                for(Lamp lamp : LampList){
                    lamp.displayOn("links_onder");
                }
                isGroupOn = true;
            }else{
                for(Lamp lamp : LampList){
                    lamp.displayOff("links_onder");
                }
            }

            if(Float.valueOf(jObject.getString("LinksBoven")) > 0){
                for(Lamp lamp : LampList){
                    lamp.displayOn("links_boven");
                }
                isGroupOn = true;
            }else{
                for(Lamp lamp : LampList){
                    lamp.displayOff("links_boven");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGroup(String statusString){

        if(statusString == null){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=status",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            setGroup(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
            });
            queue.add(stringRequest);
        }else{
            setGroup(statusString);
        }
    }
}
