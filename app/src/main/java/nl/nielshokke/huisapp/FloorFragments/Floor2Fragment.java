package nl.nielshokke.huisapp.FloorFragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-10-2017.
 */

public class Floor2Fragment extends Fragment {
    private static final String TAG = "Floor 2 Fragment";

    private boolean mLONG_CLICK = false;

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

        ImageView mask_IV = rootView.findViewById(R.id.maskView);
        mask_IV.setImageResource(R.drawable.mapfloor2);

        setItemsFloor(rootView);
        setOnTouchListenerFloor(rootView);
    }

    private void setItemsFloor(RelativeLayout rootView){

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

                        } else {

                        }
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
        HW_TV.setText("Floor 2");
    }

}

