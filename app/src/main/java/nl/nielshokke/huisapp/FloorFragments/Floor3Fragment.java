package nl.nielshokke.huisapp.FloorFragments;

import android.graphics.Bitmap;
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

        ImageView mask_IV = rootView.findViewById(R.id.maskView);
        mask_IV.setImageResource(R.drawable.mapfloor3);

        setItemsFloor(rootView);
        setOnTouchListenerFloor(rootView);
    }

    private void setItemsFloor(RelativeLayout rootView){
        lamp_M_Group = new LampGroup(getActivity(), rootView, queue, "all", R.drawable.lamp1_m_on, R.drawable.lamp1_m_off, false, false, true);

        lamp_R = new Lamp(getActivity(), lamp_M_Group, rootView, queue, "rechts", R.drawable.lamp1_r_on, R.drawable.lamp1_r_off, false, true);
        lamp_LO = new Lamp(getActivity(), lamp_M_Group, rootView, queue, "links_onder", R.drawable.lamp1_lo_on, R.drawable.lamp1_lo_off, false, true);
        lamp_LB = new Lamp(getActivity(), lamp_M_Group, rootView, queue, "links_boven", R.drawable.lamp1_lb_on, R.drawable.lamp1_lb_off, false, true);

        lamp_M_Group.addLamp(lamp_R);
        lamp_M_Group.addLamp(lamp_LO);
        lamp_M_Group.addLamp(lamp_LB);

        lamp_M_Group.updateGroup();
    }

    private void setOnTouchListenerFloor(final View rootView){

        final ImageView mask_IV = rootView.findViewById(R.id.maskView);
        final ImageView floor_IV = rootView.findViewById(R.id.floorView);
        floor_IV.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                final int evX = (int) event.getX();
                final int evY = (int) event.getY();

                if(event.getAction() == MotionEvent.ACTION_UP){
                    int touchColor = getHotspotColor (mask_IV, evX, evY);

                    //Lamp 1 M
                    if( -1236956 == touchColor) {
                        if (mLONG_CLICK) {
                            lamp_M_Group.toggleGroupmode();
                            lamp_M_Group.updateGroup();
                        } else {
                            lamp_M_Group.toggle();
                        }

                    }else if( -3584 == touchColor){
                        lamp_R.toggle();

                    }else if( -16732433 == touchColor){
                        lamp_LO.toggle();

                    }else if( -12995254 == touchColor){
                        lamp_LB.toggle();
                    }else{
                        Log.d(TAG, "Niewe kleur: " + touchColor);
                    }

                    mLONG_CLICK = false;
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mLONG_CLICK = false;
                }
                return false;
            }
        });


        floor_IV.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                mLONG_CLICK = true;
                return false;
            }
        });

    }

    private int getHotspotColor (View mask_IV, int x, int y) {
        mask_IV.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(mask_IV.getDrawingCache());
        mask_IV.setDrawingCacheEnabled(false);
        return hotspots.getPixel(x, y);
    }

    private void setFloorTitle(View rootView){
        TextView HW_TV = rootView.findViewById(R.id.HW_TV);
        HW_TV.setText("Floor 3");
    }

}