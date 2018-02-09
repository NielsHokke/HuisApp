package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import nl.nielshokke.huisapp.Notification.FrontdoorNotification;
import nl.nielshokke.huisapp.R;
import nl.nielshokke.huisapp.Dialogs.AddCardDialogFragment;
import nl.nielshokke.huisapp.Dialogs.FrontDoorDialogFragment;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Nelis on 21-10-2017.
 */

public class Frontdoor {

    private ImageView DOOR_IV;
    private ImageView ADD_CARD_IV;
    private ImageView TEST_NOTIFICATION_IV;

    private RequestQueue queue;
    private boolean isOnline;
    private boolean isInDevMode;
    private Activity mactivity;

    public Frontdoor(Activity activity, RelativeLayout rootView, RequestQueue q){
        DOOR_IV  = new ImageView(activity);
        ADD_CARD_IV  = new ImageView(activity);
        TEST_NOTIFICATION_IV = new ImageView(activity);

        DOOR_IV.setImageResource(R.drawable.deur1_off);
        DOOR_IV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        DOOR_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rootView.addView(DOOR_IV, 2);

        ADD_CARD_IV.setImageResource(R.drawable.addcard_off);
        ADD_CARD_IV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ADD_CARD_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rootView.addView(ADD_CARD_IV, 2);

        TEST_NOTIFICATION_IV.setImageResource(R.drawable.test_notification_on);
        TEST_NOTIFICATION_IV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TEST_NOTIFICATION_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        TEST_NOTIFICATION_IV.setVisibility(View.INVISIBLE);
        rootView.addView(TEST_NOTIFICATION_IV, 2);

        queue = q;
        isOnline = false;
        isInDevMode = false;

        mactivity = activity;

        checkIfOnline();
    }

    public void checkIfOnline(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.200/cgi-bin/isOnline.py",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DOOR_IV.setImageResource(R.drawable.deur1_on);
                        ADD_CARD_IV.setImageResource(R.drawable.addcard_on);
                        isOnline = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DOOR_IV.setImageResource(R.drawable.deur1_off);
                        ADD_CARD_IV.setImageResource(R.drawable.addcard_off);
                        isOnline = false;
                    }
        });
        queue.add(stringRequest);
    }

    public void click(){
        if(isOnline){
            showFrontDoorDialog();
        }else{
            checkIfOnline();
        }
    }

    public void addCardClick(){
        if(isOnline){
            showAddCardDialog();
        }else{
            checkIfOnline();
        }
    }

    public void testNotificationClick(){

        FrontdoorNotification notification = new FrontdoorNotification(mactivity.getBaseContext());
        notification.show(null,"", true);

    }

    public void simulateDingDongClick(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.200/cgi-bin/notificationTest.py",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
        queue.add(stringRequest);

    }



    public void setDevMode(Boolean devMode){
        isInDevMode = devMode;
        if(isInDevMode){
            TEST_NOTIFICATION_IV.setVisibility(View.VISIBLE);
        }
    }

    public void showFrontDoorDialog(){
        FrontDoorDialogFragment newFragment = FrontDoorDialogFragment.newInstance();
        newFragment.show(mactivity.getFragmentManager(), "dialog");
    }

    private void showAddCardDialog(){
        AddCardDialogFragment newFragment = AddCardDialogFragment.newInstance();
        newFragment.show(mactivity.getFragmentManager(), "dialog");
    }
}
