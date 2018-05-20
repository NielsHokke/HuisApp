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

public class FanSimpleDialogFragment extends DialogFragment{


    private String TAG = "FanSimpleDialogFragment";
    private RequestQueue queue;

    private String url ="http://192.168.178.198/";
    private static String subUrl;

    private  TimePicker TP;


    public static FanSimpleDialogFragment newInstance(String fanName) {
        subUrl = fanName;
        return new FanSimpleDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_fansimple, null);

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
            final Button setFanButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            setFanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RadioGroup RG = dialog.findViewById(R.id.fanRadioGroup);
                    int selectedId = RG.getCheckedRadioButtonId();
                    RadioButton selectedButton = dialog.findViewById(selectedId);
                    String speed = (String) selectedButton.getTag();

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "set/" + speed, new Response.Listener<String>() {
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

        }
    }
}
