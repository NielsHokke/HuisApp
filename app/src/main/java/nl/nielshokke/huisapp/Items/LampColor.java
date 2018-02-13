package nl.nielshokke.huisapp.Items;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import nl.nielshokke.huisapp.Dialogs.ColorLampDialogFragment;
import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 13-2-2018.
 */

public class LampColor extends Lamp {
    public LampColor(final Context context, LampGroup group, RelativeLayout rootView, RequestQueue q, String urlName, int srcOn, int srcOff, boolean on, boolean hidden, int default_x, int default_y) {
        super(context, group, rootView, q, urlName, srcOn, srcOff, on, hidden, default_x, default_y);
        Lamp_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Activity activity = (Activity) context;
                ColorLampDialogFragment newFragment = ColorLampDialogFragment.newInstance();
                newFragment.show(activity.getFragmentManager(), "dialog");
            }
        });
    }
}
