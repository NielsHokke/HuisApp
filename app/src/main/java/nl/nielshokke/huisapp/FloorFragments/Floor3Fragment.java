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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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

        View rootView = inflater.inflate(R.layout.fragment_floor, container, false);

        loadFloor((RelativeLayout) rootView);
        setFloorTitle(rootView);

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

        lamp_M_Group.updateGroup();
    }

    private void setFloorTitle(View rootView){
        TextView HW_TV = rootView.findViewById(R.id.HW_TV);
        HW_TV.setText("Floor 3");
    }

}