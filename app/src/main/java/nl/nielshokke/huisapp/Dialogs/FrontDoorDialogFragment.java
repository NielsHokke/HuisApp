package nl.nielshokke.huisapp.Dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import nl.nielshokke.huisapp.R;

/**
 * Created by Nelis on 21-10-2017.
 */

public class FrontDoorDialogFragment extends DialogFragment {

    private String TAG = "FrontDoorDialogFragment";
    private RequestQueue queue;

    public static FrontDoorDialogFragment newInstance() {
        return new FrontDoorDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogRootView = inflater.inflate(R.layout.dialogfragment_frontdoor, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Open door", null); // OnClickListener is added in onStart()
        builder.setView(dialogRootView);
        Dialog dialog = builder.create();

        queue = Volley.newRequestQueue(getActivity());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            final Button opendoorButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            opendoorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String android_id = Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.178.200/cgi-bin/openDoor.py", new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s){}
                    }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError){}
                    }){
                        @Override
                        protected Map<String, String> getParams(){
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("key", android_id);
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                }
            });

            //load connecting animation
            final Animation flash = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha);
            flash.reset();
            final TextView ConnectingText = (TextView) dialog.findViewById(R.id.ConnectingTV);
            ConnectingText.setText(getString(R.string.connecting));
            ConnectingText.startAnimation(flash);

            final WebView webView = (WebView) dialog.findViewById(R.id.WebView);
            //WebView webView = dialogRootView.findViewById(R.id.WebView);
            webView.clearCache(true);
            webView.loadUrl("http://192.168.178.200:8080/stream_simple.html");
            webView.setInitialScale(getScale(700,0));
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            webView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "webview click!");
                    ((WebView) view).reload();
                }
            });

            //on success make webview visible
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(final WebView view, String title) {
                    if(title.contains("MJPG")){
                        view.setVisibility(View.VISIBLE);
                        view.setAlpha(0);

                        view.animate().setDuration(1000).alpha(1).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }
                        });
                        opendoorButton.setEnabled(true);
                    }
                }
            });

            //on fail hide webview and load reconnect text
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.d(TAG, "onReceivedError: ");
                    view.setVisibility(View.INVISIBLE);
                     opendoorButton.setEnabled(false);

                    if(isAdded()) {
                        ConnectingText.setText(getString(R.string.cant_connect));
                        ConnectingText.clearAnimation();
                        ConnectingText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isAdded()) {
                                    webView.reload();
                                    ConnectingText.startAnimation(flash);
                                    ConnectingText.setText(getString(R.string.connecting));
                                }
                            }
                        });
                    }
                }
            });

            opendoorButton.setEnabled(false);
        }
    }

    private int getScale(int PIC_WIDTH, int padding){
        //function used for scaling webview
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        Double val = (double) width / (double) PIC_WIDTH;
        val = val * 100d;
        return val.intValue() - padding;
    }
}
