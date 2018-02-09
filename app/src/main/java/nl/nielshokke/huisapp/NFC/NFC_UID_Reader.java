package nl.nielshokke.huisapp.NFC;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.ref.WeakReference;

/*
 * Created by Nelis on 7-7-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NFC_UID_Reader implements NfcAdapter.ReaderCallback {

    private static final String TAG = "NFC_UID_Reader";

    // Weak reference to prevent retain loop. mAccountCallback is responsible for exiting
    // foreground mode before it becomes invalid (e.g. during onPause() or onStop()).
    private WeakReference<NFC_UID_Callback> mNFC_UID_Callback;

    public interface NFC_UID_Callback {
        void onNFC_UID_Received(String NFC_UID);
    }

    public NFC_UID_Reader(NFC_UID_Callback nfc_uid_callback) {
        mNFC_UID_Callback = new WeakReference<NFC_UID_Callback>(nfc_uid_callback);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered ID: " + ByteArrayToHexString(tag.getId()));

        mNFC_UID_Callback.get().onNFC_UID_Received(ByteArrayToHexString(tag.getId()));
    }

    private static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}

