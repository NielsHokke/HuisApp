package nl.nielshokke.huisapp.Dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flask.colorpicker.ColorPickerView;

import java.util.HashMap;
import java.util.Map;

import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 11-2-2018.
 */

public class ColorLampDialogFragment extends DialogFragment {

    private String TAG = "FrontDoorDialogFragment";
    private RequestQueue queue;
    private View dialogRootView;

    public static ColorLampDialogFragment newInstance() {
        return new ColorLampDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogRootView = inflater.inflate(R.layout.dialogfragment_colorlamp, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogRootView);
        Dialog dialog = builder.create();

        queue = Volley.newRequestQueue(getActivity());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        final ColorPickerView ColorP = dialogRootView.findViewById(R.id.color_picker_view);

        Button SetColor_BT = dialogRootView.findViewById(R.id.button1);
        Button FadeTo_BT = dialogRootView.findViewById(R.id.button2);
        Button Rainbow_BT = dialogRootView.findViewById(R.id.button3);
        Button Clear_BT = dialogRootView.findViewById(R.id.button4);

        SetColor_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int R = ColorP.getSelectedColor() >> 16 & 0xFF;
                int G = ColorP.getSelectedColor() >> 8 & 0xFF;
                int B = ColorP.getSelectedColor() >> 0 & 0xFF;

                String SR = String.format("%03d", R);
                String SG = String.format("%03d", G);
                String SB = String.format("%03d", B);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.202/cgi-bin/RGB_Bridge.py?cmd=setColor_" + SR + "_" + SG + "_"  + SB, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s){}
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError){}
                });
                queue.add(stringRequest);
            }
        });

        FadeTo_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int R = ColorP.getSelectedColor() >> 16 & 0xFF;
                int G = ColorP.getSelectedColor() >> 8 & 0xFF;
                int B = ColorP.getSelectedColor() >> 0 & 0xFF;

                String SR = String.format("%03d", R);
                String SG = String.format("%03d", G);
                String SB = String.format("%03d", B);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.202/cgi-bin/RGB_Bridge.py?cmd=fadeToColor_" + SR + "_" + SG + "_"  + SB + "_000_010_000", new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s){}
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError){}
                });
                queue.add(stringRequest);
            }
        });

        Rainbow_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.202/cgi-bin/RGB_Bridge.py?cmd=rainbow", new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s){}
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError){}
                });
                queue.add(stringRequest);
            }
        });

        Clear_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.202/cgi-bin/RGB_Bridge.py?cmd=clear", new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s){}
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError){}
                });
                queue.add(stringRequest);
            }
        });
    }
}
