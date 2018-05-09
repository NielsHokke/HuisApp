package nl.nielshokke.huisapp.FloorFragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import nl.nielshokke.huisapp.Items.Frontdoor;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-10-2017.
 */

public class Floor1Fragment  extends Fragment {
    private static final String TAG = "Floor 1 Fragment";
    private static final String OPEN_CAMERA_TAG = "frontdooroOpenCamera";

    private boolean mLONG_CLICK = false;

    private RequestQueue queue;
    private Frontdoor FRONT_DOOR;

    public Floor1Fragment() {
        // Required empty public constructor
    }

    public static Floor1Fragment newInstance() {
        return new Floor1Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FRONT_DOOR.setDevMode(sharedPref.getBoolean("dev_options", false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_floor, container, false);

        loadFloor((RelativeLayout) rootView);
        setFloorTitle(rootView);

        setTempHumid(rootView);
        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            public void run() {
                setTempHumid(rootView);
            }
        };
        timer.scheduleAtFixedRate(refresher, 0,5000);

        if(getArguments().getBoolean(OPEN_CAMERA_TAG, false)){
            FRONT_DOOR.showFrontDoorDialog();
        }

        return rootView;
    }

    private void loadFloor(RelativeLayout rootView){

        ImageView floor_IV = rootView.findViewById(R.id.floorView);
        if(floor_IV != null) {
//            ((BitmapDrawable)floor_IV.getDrawable()).getBitmap().recycle();
            floor_IV.setImageResource(R.drawable.floor1);
        }

        setItemsFloor(rootView);
    }

    private void setItemsFloor(RelativeLayout rootView){
        FRONT_DOOR = new Frontdoor(getActivity(), rootView, queue);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FRONT_DOOR.setDevMode(sharedPref.getBoolean("dev_options", false));
    }

    private void setFloorTitle(View rootView){
        TextView HW_TV = rootView.findViewById(R.id.HW_TV);
        HW_TV.setText("Floor 1");
    }

    private void setTempHumid(View rootView){
        final TextView TX_TH = rootView.findViewById(R.id.TX_TH);
//        Log.d("TempHumid", "TempHumid status request: http://192.168.178.200/cgi-bin/tempHumid.py");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.200/cgi-bin/tempHumid.py",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObject = new JSONObject(response);
//                            Log.d("TempHumid", "TempHumid json: " + jObject);
                            float Temp = Float.valueOf(jObject.getString("Temperature"));
                            float Humid = Float.valueOf(jObject.getString("Humidity"));
                            String TempS = String.format(Locale.ENGLISH, "%.01f", Float.valueOf(jObject.getString("Temperature")));
                            String HumidS = String.format(Locale.ENGLISH, "%.0f", Float.valueOf(jObject.getString("Humidity")));
                            TX_TH.setText("Temp:\t\t"+ TempS + "°C\nHumid:\t" + HumidS + "%");
//                            Log.d("TempHumid", "Received temp: " + TempS + ", humid: " + HumidS);
                        } catch (JSONException e) {
//                            Log.d("TempHumid", "Response is not json?");
                            TX_TH.setText("");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("TempHumid", "We've got not or error response");
                error.printStackTrace();
                TX_TH.setText("");
            }
        });
        queue.add(stringRequest);
//        Log.d("TempHumid", "StringRequest: "+stringRequest);

    }
}
