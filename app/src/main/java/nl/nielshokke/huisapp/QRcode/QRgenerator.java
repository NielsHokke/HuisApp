package nl.nielshokke.huisapp.QRcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Nelis on 13-11-2017.
 */


public class QRgenerator {

// encrypted [ (ID of generator) (time of creation) (time of termination) (name of reciever)
    public static Bitmap generateQR(String data){

        Log.d("generateQR", "Generating Qr-code for: " + data);

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

    public static String generateKey(Activity activity, String keyString, String name){
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);

            final String android_id = Settings.Secure.getString(activity.getContentResolver(),Settings.Secure.ANDROID_ID);
            json.put("made_by", android_id);

            Date currentTime = Calendar.getInstance().getTime();
            json.put("made_on", currentTime.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String data = json.toString();
        Log.d("generate key", data);

        byte[] key = keyString.getBytes();
        Log.d("generate key", "key lenght: " + key.length);
        byte[] ct = {};
        try {
            ct = encrypt(key, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(ct, Base64.NO_WRAP);
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

}

