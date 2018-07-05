package nl.nielshokke.huisapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;

import nl.nielshokke.huisapp.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nelis on 5-7-2018.
 */

public class GetQRkeyDialogFragment extends DialogFragment {

    Dialog dialog;

    private static final String QR_KEY = "QR_CODE_KEY";

    public static GetQRkeyDialogFragment newInstance() {
        return new GetQRkeyDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_qr_key, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setPositiveButton("Save", null); // OnClickListener is added in onStart()
        builder.setView(dialogRootView);
        dialog = builder.create();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        final EditText keyET = dialog.findViewById(R.id.KeyET);

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            final Button saveBT = dialog.findViewById(R.id.SaveBT);
            saveBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO save key to prefs
                    String key = keyET.getText().toString();
                    int key_size = key.length();
                    if(key_size == 32){
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(QR_KEY, MODE_PRIVATE).edit();
                        editor.putString(QR_KEY, key);
                        editor.apply();

                        Toast.makeText(getActivity(), "key saved", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }else{
                        Toast.makeText(getActivity(), "key must be 32 characters long, is " + key_size, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
