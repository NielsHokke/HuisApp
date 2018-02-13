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

    List<Lamp> LampList;
    boolean isInGroupMode;

    public LampGroup(final Context context, RelativeLayout rootView, RequestQueue q, String urlName, int srcOn, int srcOff, boolean on, boolean hidden, boolean groupMode, int default_x, int default_y){
        super(context, null, rootView, q, urlName, srcOn, srcOff, on, hidden, default_x, default_y);
        LampList = new ArrayList<>();
        isInGroupMode = groupMode;
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
        if(isInGroupMode){
            if(isOn()){
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=" + subUrl + "_uit",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                setOn(false);
                                setView();
                                for(Lamp lamp : LampList){
                                    lamp.displayOff("");
                                }
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
                                setOn(true);
                                setView();
                                for(Lamp lamp : LampList){
                                    lamp.displayOn("");
                                }
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
    }

    @Override
    public void updateGroup(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url  + "?cmd=status",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);

                            if((jObject.getString("all").equals("on") && isInGroupMode) || (jObject.getString("midden").equals("on") && !isInGroupMode)){
                                LampGroup.super.displayOn("");
                            }else if((jObject.getString("all").equals("off") && isInGroupMode)|| (jObject.getString("midden").equals("off") && !isInGroupMode)){
                                LampGroup.super.displayOff("");
                            }

                            if(jObject.getString("rechts").equals("on")){
                                for(Lamp lamp : LampList){
                                    lamp.displayOn("rechts");
                                }
                            }else{
                                for(Lamp lamp : LampList){
                                    lamp.displayOff("rechts");
                                }
                            }

                            if(jObject.getString("midden").equals("on")){
                                for(Lamp lamp : LampList){
                                    lamp.displayOn("midden");
                                }
                            }else{
                                for(Lamp lamp : LampList){
                                    lamp.displayOff("midden");
                                }
                            }

                            if(jObject.getString("links_onder").equals("on")){
                                for(Lamp lamp : LampList){
                                    lamp.displayOn("links_onder");
                                }
                            }else{
                                for(Lamp lamp : LampList){
                                    lamp.displayOff("links_onder");
                                }
                            }

                            if(jObject.getString("links_boven").equals("on")){
                                for(Lamp lamp : LampList){
                                    lamp.displayOn("links_boven");
                                }
                            }else{
                                for(Lamp lamp : LampList){
                                    lamp.displayOff("links_boven");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(stringRequest);
    }
}
