package nl.nielshokke.huisapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 19-4-2018.
 */

public class FanDialogFragment extends DialogFragment{

    private String TAG = "FanDialogFragment";
    private RequestQueue queue;

    private String url ="http://192.168.178.202/cgi-bin/GrootLightBridge.py";
    private static String subUrl;

    private  TimePicker TP;


    public static FanDialogFragment newInstance(String fanName) {
        subUrl = fanName;
        return new FanDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_fan, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("set fan", null); // OnClickListener is added in onStart()
        builder.setView(dialogRootView);
        Dialog dialog = builder.create();

        queue = Volley.newRequestQueue(getActivity());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            final Button opendoorButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            opendoorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int hours = 0;
                    int minutes = 0;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        hours = TP.getHour();
                        minutes = TP.getMinute();
                    }else{
                        hours = TP.getCurrentHour();
                        minutes = TP.getCurrentMinute();
                    }

                    minutes += hours * 60;

                    RadioGroup RG = dialog.findViewById(R.id.fanRadioGroup);
                    int selectedId = RG.getCheckedRadioButtonId();
                    RadioButton selectedButton = dialog.findViewById(selectedId);
                    String speed = (String) selectedButton.getTag();

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?cmd=" + subUrl + "_" + speed + "_" + minutes, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            dialog.cancel();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {

                    };
                    queue.add(stringRequest);
                }
            });

            TP = dialog.findViewById(R.id.timePicker);

            TP.setIs24HourView(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TP.setHour(0);
                TP.setMinute(30);
            }


            final int id = Resources.getSystem().getIdentifier("ampm_layout", "id", "android");
            final View amPmLayout = TP.findViewById(id);
            if(amPmLayout != null) {
                amPmLayout.setVisibility(View.GONE);
            }

            final int hourID = Resources.getSystem().getIdentifier("hours", "id", "android");
            final TextView hourText = (TextView) TP.findViewById(hourID);

            final int sepID = Resources.getSystem().getIdentifier("separator", "id", "android");
            final TextView sepText = (TextView) TP.findViewById(sepID);

            final int minutesID = Resources.getSystem().getIdentifier("minutes", "id", "android");
            final TextView minutesText = (TextView) TP.findViewById(minutesID);



            if(hourText != null) {
                hourText.setTextColor(getResources().getColor(R.color.gray));
            }

            if(sepText != null) {
                sepText.setTextColor(getResources().getColor(R.color.gray));
            }

            if(minutesText != null) {
                minutesText.setTextColor(getResources().getColor(R.color.gray));
            }

            final int toggleID = Resources.getSystem().getIdentifier("toggle_mode", "id", "android");
            final View toggleBt = TP.findViewById(toggleID);
            toggleBt.setVisibility(View.GONE);
        }
    }
}
