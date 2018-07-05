package nl.nielshokke.huisapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import nl.nielshokke.huisapp.QRcode.QRgenerator;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 20-5-2018.
 */

public class GenerateQrDialogFragment  extends DialogFragment{

    private String TAG = "GenerateQrDialogFragment";
    private RequestQueue queue;

    private TimePicker TP;

    private static String KEY_IV;

    public static GenerateQrDialogFragment newInstance(String key_IV) {
        if(key_IV.length() < 32){
            throw new IllegalArgumentException("QR_Code key < 32");
        }
        KEY_IV = key_IV;
        return new GenerateQrDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_generate_qr, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

            final EditText recipientET = dialog.findViewById(R.id.KeyET);
            final EditText commentET = dialog.findViewById(R.id.commentET);

            final Button generateButton = dialog.findViewById(R.id.generateBT);
            generateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO generate QR-code
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

                    Calendar dateTime = Calendar.getInstance();
                    dateTime.add(Calendar.MINUTE, minutes);
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String time = format1.format(dateTime.getTime());

                    String recipient = recipientET.getText().toString();
                    String comment = commentET.getText().toString();

                    Log.d(TAG, time + ", " + recipient + ", " + comment);

                    if(recipient.equals("")){
                        Toast.makeText(getActivity(), "Must enter recipient name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String key = KEY_IV.substring(0, 16); // 128 bit key
                    String IV = KEY_IV.substring(16, 32);; // 16 bytes IV
                    Log.d(TAG, "key: " + key + " IV: " + IV);
                    String cipherText = QRgenerator.generateCT(getActivity(),key, IV, recipient, time, comment);

                    Log.d(TAG, "cipherText: " + cipherText);

                    Bitmap QR_code = QRgenerator.generateQR(cipherText);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    QR_code.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), QR_code, "QR_code", null);
                    Uri imageUri = Uri.parse(path);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    String message = "Hi "+ recipient + ",\nThis QR-Code will grant you access to the DroomKeuken from now till " + time + ".\nHold the QR-Code in front of the camera. The camera can be found behind the glass in the top right corner of the frontdoor.";
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    sendIntent.setType("image/jpeg");
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.setPackage("com.whatsapp");
                    getActivity().startActivity(sendIntent);

                    dialog.cancel();
                }
            });

            TP = dialog.findViewById(R.id.timePicker);

            TP.setIs24HourView(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TP.setMinute(0);
                TP.setHour(0);
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
