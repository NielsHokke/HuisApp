package nl.nielshokke.huisapp.QRcode;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Nelis on 13-11-2017.
 */


public class QRgenerator {

    private static String TAG = "generateQR";
    private static final String USERNAME = "pref_Username";

// encrypted [ (ID of generator) (time of creation) (time of termination) (name of reciever)
    public static Bitmap generateQR(String data){

        Log.d(TAG, "Generating Qr-code for: " + data);

        QRCodeWriter writer = new QRCodeWriter();
        Bitmap bmp = null;
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static String generateCT(Activity activity, String key, String IV, String recipient, String time, String Comment){
        JSONObject json = new JSONObject();
        try {
            json.put("R", recipient);

            json.put("VT", time);

            json.put("C", Comment);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            String username = sharedPref.getString(USERNAME, "");
            if(username.equals("")){
                throw new IllegalArgumentException("No USERNAME defined in shared prefs");
            }
            json.put("MB", username);

            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            json.put("MO", dateFormat.format(date));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String data = json.toString();
        Log.d(TAG, data);
        Log.d(TAG, "data size: " + data.length());

        return encrypt(key, IV, data);
    }

    private static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted,  Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}

