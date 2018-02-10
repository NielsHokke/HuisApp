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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_floor, container, false);

        loadFloor((RelativeLayout) rootView);
        setFloorTitle(rootView);

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
        FRONT_DOOR.setDevMode(true);
    }

//    @SuppressLint("ClickableViewAccessibility")
//    private void setOnTouchListenerFloor(final View rootView){
//
//        final ImageView mask_IV = rootView.findViewById(R.id.maskView);
//        final ImageView floor_IV = rootView.findViewById(R.id.floorView);
//        floor_IV.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//
//                final int evX = (int) event.getX();
//                final int evY = (int) event.getY();
//
//                if(event.getAction() == MotionEvent.ACTION_UP){
//                    int touchColor = getHotspotColor (mask_IV, evX, evY);
//
//                    //Lamp 1 M
//                    if( -1236956 == touchColor) {
//                        if (mLONG_CLICK) {
//
//                        } else {
//                            FRONT_DOOR.click();
//                        }
//                    }else if(-9847483 == touchColor){
//                        FRONT_DOOR.addCardClick();
//
//                    }else if(-595180 == touchColor){
////                        FRONT_DOOR.testNotificationClick();
//                        FRONT_DOOR.simulateDingDongClick();
//
//                    }else{
//                        Log.d(TAG, "Niewe kleur: " + touchColor);
//                    }
//                    mLONG_CLICK = false;
//                    return true;
//                }else if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    mLONG_CLICK = false;
//                }
//                return false;
//            }
//        });
//
//        floor_IV.setOnLongClickListener(new View.OnLongClickListener(){
//
//            @Override
//            public boolean onLongClick(View view) {
//                mLONG_CLICK = true;
//                return false;
//            }
//        });
//
//    }

    private int getHotspotColor (View mask_IV, int x, int y) {
        mask_IV.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(mask_IV.getDrawingCache());
        mask_IV.setDrawingCacheEnabled(false);
        return hotspots.getPixel(x, y);
    }

    private void setFloorTitle(View rootView){
        TextView HW_TV = rootView.findViewById(R.id.HW_TV);
        HW_TV.setText("Floor 1");
    }

}
