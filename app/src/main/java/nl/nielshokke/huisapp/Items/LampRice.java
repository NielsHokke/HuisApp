package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import nl.nielshokke.huisapp.Dialogs.ColorLampDialogFragment;
import nl.nielshokke.huisapp.R;

public class LampRice extends Lamp {
    public LampRice(final Context context, LampGroup group, RelativeLayout rootView, RequestQueue q, String urlName, int srcOn, int srcOff, boolean on, boolean hidden, int default_x, int default_y) {
        super(context, group, rootView, q, urlName, srcOn, srcOff, on, hidden, default_x, default_y);
        url ="http://192.168.178.205/cgi-bin/GrootLightBridge.py";
//        Log.d("LampRice", "LampRice created");
    }

}
