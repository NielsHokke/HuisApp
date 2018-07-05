package nl.nielshokke.huisapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nl.nielshokke.huisapp.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nelis on 5-7-2018.
 */

public class GetUserInfoDialogFragment extends DialogFragment {

    Dialog dialog;

    private static final String QR_KEY = "QR_CODE_KEY";
    private static final String USERNAME = "pref_Username";

    private static String Title = "";
    private static String Text = "";


    public static GetUserInfoDialogFragment newInstance(String title, String text) {
        Title = title;
        Text = text;
        return new GetUserInfoDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_user_info, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogRootView);
        dialog = builder.create();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        final EditText nameET = dialog.findViewById(R.id.NameET);

        final TextView titleTV = dialog.findViewById(R.id.ld_top_title);
        final TextView textTV = dialog.findViewById(R.id.TV2);

        titleTV.setText(Title);
        textTV.setText(Text);

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            final Button saveBT = dialog.findViewById(R.id.SaveBT);
            saveBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO save key to prefs
                    String name = nameET.getText().toString();
                    int name_size = name.length();
                    if(name_size > 0){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(USERNAME, name);
                        editor.apply();

                        Toast.makeText(getActivity(), "name saved", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }else{
                        Toast.makeText(getActivity(), "Name must be longer", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}