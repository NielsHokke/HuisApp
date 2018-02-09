package nl.nielshokke.huisapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.nielshokke.huisapp.NFC.NFC_UID_Reader;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 23-10-2017.
 */

public class AddCardDialogFragment extends DialogFragment implements NFC_UID_Reader.NFC_UID_Callback {

    private String TAG = "AddCardDialogFragment";
    private RequestQueue queue;

    private TextView CardIDET;
    private EditText CardNameET;
    private AutoCompleteTextView UserNameET;

    private String[] names = {""};
    private JSONArray UserData;

    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public NFC_UID_Reader mNFC_UID_Reader;

    public static AddCardDialogFragment newInstance() {
        return new AddCardDialogFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_addcard, null);
        CardNameET = dialogRootView.findViewById(R.id.card_title);
        CardIDET = dialogRootView.findViewById(R.id.card_account_field);
        UserNameET = dialogRootView.findViewById(R.id.UserNameET);

        Dialog dialog = BuildDialog(dialogRootView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        queue = Volley.newRequestQueue(getActivity());
        getUsers(queue, UserNameET);

        mNFC_UID_Reader = new NFC_UID_Reader(this);
        enableReaderMode();
        Toast.makeText(getActivity(), "Hold NFC tag to back of phone to scan!", Toast.LENGTH_LONG).show();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            final Button addCardButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            addCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataIsValid()) {
                        sendData();
                    }
                }
            });
        }
    }

    private Dialog BuildDialog(View DialogView){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Add card", null);
        builder.setView(DialogView);

        return builder.create();
    }

    private void getUsers(final RequestQueue queue, final AutoCompleteTextView UserNameET){
        String url = "http://192.168.178.100/deur_authenticatie/public/App/getUsers";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                UserData = new JSONArray(response);
                                names = new String[UserData.length()];
                                for (int i = 0; i < UserData.length(); i++) {
                                    JSONObject row = UserData.getJSONObject(i);
                                    int id = row.getInt("id");
                                    String first_name = row.getString("first_name");
                                    String last_name = row.getString("last_name");

                                    Log.d(TAG, "id: " + id + ", first_name: " + first_name + ", last_name: " + last_name);
                                    names[i] = first_name + " " + last_name;

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            UserNameET.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, names));
                            UserNameET.setThreshold(1);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        queue.add(stringRequest);
    }

    private boolean dataIsValid() {
        Boolean isVallid = true;

        if(CardNameET.getText().toString().length() == 0){
            isVallid = false;
            Toast.makeText(getActivity(), "Please fill in a Card Title", Toast.LENGTH_SHORT).show();
        }else if(UserNameET.getText().toString().split(" ").length < 2){
            isVallid = false;
            Toast.makeText(getActivity(), "Please fill in a first and last name", Toast.LENGTH_SHORT).show();
        }else if(CardIDET.getText().toString().equals("0000000000")){
            isVallid = false;
            Toast.makeText(getActivity(), "Please scan a NFC tag", Toast.LENGTH_SHORT).show();
        }
        return isVallid;
    }

    private void sendData(){
        String url = "http://192.168.178.100/deur_authenticatie/public/App/addCardUser";
        JSONObject json = new JSONObject();
        String Name = UserNameET.getText().toString();

        String[] separated = Name.split(" ");
        String FirstName = separated[0];
        String LastName = separated[1];

        for(int i=2; i<separated.length; i++){
            LastName += " " + separated[i];
        }

        int index = -1;
        for (int i=0;i<names.length;i++) {
            if (names[i].equals(Name)) {
                index = i;
                break;
            }
        }
        try {
            if(index == -1){
//                New user JSON
//                {"serial":"516526248",
//                 "card_name":"TestyJsown",
//                 "create_person":1,
//                 "first_name":"Je Fatsige",
//                 "last_name":"Moedertje"}

                json.put("serial", CardIDET.getText().toString());
                json.put("card_name", CardNameET.getText().toString());
                json.put("create_person", 1);
                json.put("first_name", FirstName);
                json.put("last_name", LastName);

            }else{
//                id user JSON
//                {"person_id", id,
//                 "serial":"5165262484",
//                 "card_name":"TestJson"}

                JSONObject row = UserData.getJSONObject(index);
                int id = row.getInt("id");

                json.put("serial", CardIDET.getText().toString());
                json.put("card_name", CardNameET.getText().toString());
                json.put("person_id", id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest JsonRequest = new JsonObjectRequest(url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response: " + response);
                try {
                    Toast.makeText(getActivity(), response.getString("Success"), Toast.LENGTH_LONG).show();
                    AlertDialog dialog = (AlertDialog) getDialog();
                    dialog.dismiss();
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Toast.makeText(getActivity(), "Error: " + response.getString("Error"), Toast.LENGTH_LONG).show();
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error: " + error.getMessage());
            }
        });
        queue.add(JsonRequest);
    }

    @Override
    public void onNFC_UID_Received(final String NFC_UID) {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CardIDET.setText(NFC_UID);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(getActivity());
        if (nfc != null) {
            nfc.enableReaderMode(getActivity(), mNFC_UID_Reader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(getActivity());
        if (nfc != null) {
            nfc.disableReaderMode(getActivity());
        }
    }
}
