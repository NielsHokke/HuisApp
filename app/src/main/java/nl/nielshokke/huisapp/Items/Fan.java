package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import nl.nielshokke.huisapp.Dialogs.FanDialogFragment;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 19-4-2018.
 */

public class Fan {

    private static final String TAG = "Lamp";

    private boolean isOn;
    private boolean isHidden;
    private boolean isOnline;

    ImageView Fan_IV;
    private int sourceOn;
    private int sourceOff;



    String url ="http://192.168.178.202/cgi-bin/GrootLightBridge.py";
    String subUrl;
    RequestQueue queue;
    Activity myActivity;


    public Fan(final Activity activity, RelativeLayout rootView, RequestQueue q,  String urlName, int srcOn, int srcOff, boolean on, boolean hidden, int default_x, int default_y){
        Fan_IV = new ImageView(activity);
        isHidden = hidden;
        isOn = on;
        isOnline = false;
        sourceOn = srcOn;
        sourceOff = srcOff;
        queue = q;
        subUrl = urlName;

        myActivity = activity;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.floorView);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.floorView);
        Fan_IV.setLayoutParams(layoutParams);
        Fan_IV.setX(default_x);
        Fan_IV.setY(default_y);
        Fan_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Fan_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Click();
            }
        });
        rootView.addView(Fan_IV, 2);

        checkIfOnline();
    }

    private void checkIfOnline(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.202/cgi-bin/GrootLightBridge.py?cmd=test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isOnline = true;
                        setView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isOnline = false;
                setView();
            }
        });
        queue.add(stringRequest);
    }

    private void setView(){
        if(isOnline){
            Fan_IV.setImageResource(sourceOn);
        }else{
            Fan_IV.setImageResource(sourceOff);
        }

        if(isHidden){
            Fan_IV.setVisibility(View.INVISIBLE);
        }else{
            Fan_IV.setVisibility(View.VISIBLE);
        }
    }


    private void Click(){
        if(isOnline){
            showFanDialog();
        }else{
            checkIfOnline();
        }
    }

    private void showFanDialog(){
        FanDialogFragment newFragment = FanDialogFragment.newInstance(subUrl);
        newFragment.show(myActivity.getFragmentManager(), "dialog");
    }


}
