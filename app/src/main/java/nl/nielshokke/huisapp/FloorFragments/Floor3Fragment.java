package nl.nielshokke.huisapp.FloorFragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import nl.nielshokke.huisapp.Items.Fan;
import nl.nielshokke.huisapp.Items.Lamp;
import nl.nielshokke.huisapp.Items.LampColor;
import nl.nielshokke.huisapp.Items.LampGroup;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-10-2017.
 */

public class Floor3Fragment extends Fragment {

    private static final String TAG = "Floor  3 Fragment";
    private LampGroup lamp_M_Group;
    private Lamp lamp_R;
    private Lamp lamp_LO;
    private Lamp lamp_LB;
    private Lamp lamp_Co;

    private Fan fan;

    private boolean mLONG_CLICK = false;

    private RequestQueue queue;

    public Floor3Fragment() {
        // Required empty public constructor
    }

    public static Floor3Fragment newInstance() {
        return new Floor3Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
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

        return rootView;
    }

    private void loadFloor(RelativeLayout rootView){
        ImageView floor_IV = rootView.findViewById(R.id.floorView);
        floor_IV.setImageResource(R.drawable.floor3);
        setItemsFloor(rootView);
    }

    private void setItemsFloor(RelativeLayout rootView){
        lamp_M_Group = new LampGroup(getActivity(), rootView, queue, "all", R.drawable.lamp_groot_on, R.drawable.lamp_groot_off, false, false, true, 0, -500);

        lamp_R = new Lamp(getActivity(), lamp_M_Group, rootView, queue, "rechts", R.drawable.lamp_klein_on, R.drawable.lamp_klein_off, false, true, 300, -500);
        lamp_LO = new Lamp(getActivity(), lamp_M_Group, rootView, queue, "links_onder", R.drawable.lamp_klein_on, R.drawable.lamp_klein_off, false, true,-300, -575);
        lamp_LB = new Lamp(getActivity(), lamp_M_Group, rootView, queue, "links_boven", R.drawable.lamp_klein_on, R.drawable.lamp_klein_off, false, true,-300, -425);

        lamp_Co = new LampColor(getActivity(), lamp_M_Group, rootView, queue, "niks", R.drawable.lamp_color_on, R.drawable.lamp_klein_off, true, false, 0,-650);

        lamp_M_Group.addLamp(lamp_R);
        lamp_M_Group.addLamp(lamp_LO);
        lamp_M_Group.addLamp(lamp_LB);

        fan = new Fan(getActivity(), rootView, queue, "set_fan", R.drawable.fan_on, R.drawable.fan_off, false, false, -150, -340);

        lamp_M_Group.updateGroup(null);
    }

    private void setFloorTitle(View rootView){
        TextView HW_TV = rootView.findViewById(R.id.HW_TV);
        HW_TV.setText("Floor 3");
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