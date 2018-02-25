package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
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
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

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
    private ImageView GENERATE_QR_CODE;

    private RequestQueue queue;
    private boolean isOnline;
    private boolean isInDevMode;
    private Activity mactivity;

    public Frontdoor(final Activity activity, RelativeLayout rootView, RequestQueue q){
        DOOR_IV  = new ImageView(activity);
        ADD_CARD_IV  = new ImageView(activity);
        TEST_NOTIFICATION_IV = new ImageView(activity);
        GENERATE_QR_CODE = new ImageView(activity);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.floorView);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.floorView);

        DOOR_IV.setImageResource(R.drawable.deur_off);
        DOOR_IV.setLayoutParams(layoutParams);
        DOOR_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        DOOR_IV.setX(75);
        DOOR_IV.setY(-700);
        DOOR_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click();
            }
        });
        rootView.addView(DOOR_IV, 2);

        ADD_CARD_IV.setImageResource(R.drawable.add_card_off);
        ADD_CARD_IV.setLayoutParams(layoutParams);
        ADD_CARD_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ADD_CARD_IV.setX(-150);
        ADD_CARD_IV.setY(-700);
        ADD_CARD_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCardClick();
            }
        });
        rootView.addView(ADD_CARD_IV, 2);

        TEST_NOTIFICATION_IV.setImageResource(R.drawable.test_notification_on);
        TEST_NOTIFICATION_IV.setLayoutParams(layoutParams);
        TEST_NOTIFICATION_IV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        TEST_NOTIFICATION_IV.setX(-150);
        TEST_NOTIFICATION_IV.setY(-475);
        TEST_NOTIFICATION_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testNotificationClick();
            }
        });
        rootView.addView(TEST_NOTIFICATION_IV, 2);


        GENERATE_QR_CODE.setImageResource(R.drawable.generate_qr_on);
        GENERATE_QR_CODE.setLayoutParams(layoutParams);
        GENERATE_QR_CODE.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GENERATE_QR_CODE.setX(-150);
        GENERATE_QR_CODE.setY(-250);
        GENERATE_QR_CODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Frontdoor", "generate QR-code");
            }
        });
        rootView.addView(GENERATE_QR_CODE, 2);




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
                        //TODO recylcle
//                        ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
                        DOOR_IV.setImageResource(R.drawable.deur_on);
                        ADD_CARD_IV.setImageResource(R.drawable.add_card_on);
                        isOnline = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DOOR_IV.setImageResource(R.drawable.deur_off);
                        ADD_CARD_IV.setImageResource(R.drawable.add_card_off);
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
        Log.d("notification", "isInDevMode: " + isInDevMode + " isOnline: " + isOnline);
        if(isInDevMode && isOnline){
            simulateDingDongClick();
        }else{
            FrontdoorNotification notification = new FrontdoorNotification(mactivity.getBaseContext());
            notification.show(null,"", true, false);
            checkIfOnline();
        }
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
