package nl.nielshokke.huisapp.FloorFragments;

import android.annotation.SuppressLint;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import nl.nielshokke.huisapp.Items.Fan;
import nl.nielshokke.huisapp.Items.Lamp;
import nl.nielshokke.huisapp.Items.LampColor;
import nl.nielshokke.huisapp.Items.LampGroup;
import nl.nielshokke.huisapp.Items.LampRice;
import nl.nielshokke.huisapp.Items.ProMax;
import nl.nielshokke.huisapp.QRcode.QRgenerator;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-10-2017.
 */

public class Floor2Fragment extends Fragment {
    private static final String TAG = "Floor 2 Fragment";

    private LampGroup lamp_Rice_Group;
    private LampRice lamp_Amber;
    // private Lamp lamp_RColor;
    private ProMax pm_Amplifier;
    private ProMax lamp_DeskPM;

    private RequestQueue queue;

    public Floor2Fragment() {
        // Required empty public constructor
    }

    public static Floor2Fragment newInstance() {
        return new Floor2Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_floor, container, false);

        loadFloor((RelativeLayout) rootView);
        setFloorTitle(rootView);


        return rootView;
    }

    private void loadFloor(RelativeLayout rootView){
        ImageView floor_IV = rootView.findViewById(R.id.floorView);
        floor_IV.setImageResource(R.drawable.floor2);
        setItemsFloor(rootView);
//        Log.d("FloorLoader", "Floor 2 is loaded");
    }

    private void setItemsFloor(RelativeLayout rootView) {
        //lamp_Rice_Group = new LampGroup(getActivity(), rootView, queue, "all", R.drawable.lamp_groot_on, R.drawable.lamp_groot_off, false, true, false, 0, -500);

        lamp_Amber = new LampRice(getActivity(), null, rootView, queue, "Amber", R.drawable.lamp_klein_on_amber, R.drawable.lamp_klein_off, false, false, 400, -440);

        //lamp_RColor = new LampColor(getActivity(), lamp_Rice_Group, rootView, queue, "niks", R.drawable.lamp_color_on, R.drawable.lamp_klein_off, true, false, 0,-650);

        //lamp_Rice_Group.addLamp(lamp_Amber);
        lamp_Amber.updateLamp(null);
        //lamp_Rice_Group.updateGroup(null);

        pm_Amplifier = new ProMax(getActivity(), rootView, queue, "012705ED90", "01249BA5D0", R.drawable.speaker_on, R.drawable.speaker_off, false, false, -35, -615);
        lamp_DeskPM = new ProMax(getActivity(), rootView, queue, "01E5EAFB90", "01E65A94D0", R.drawable.lamp_klein_on, R.drawable.lamp_klein_off, false, false, -400, -520);

    }
    private void setFloorTitle(View rootView){
        TextView HW_TV = rootView.findViewById(R.id.HW_TV);
        HW_TV.setText("Floor 2");
    }

}

