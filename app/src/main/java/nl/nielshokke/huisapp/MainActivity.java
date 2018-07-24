package nl.nielshokke.huisapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import nl.nielshokke.huisapp.FloorFragments.Floor1Fragment;
import nl.nielshokke.huisapp.FloorFragments.Floor2Fragment;
import nl.nielshokke.huisapp.FloorFragments.Floor3Fragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String OPEN_CAMERA_TAG = "frontdooroOpenCamera";

    private FragmentStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("voordeur");


        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsStatePagerAdapter);

        if(OPEN_CAMERA_TAG.equals(getIntent().getAction())){
            //mSectionsStatePagerAdapter.setOpenCamera();
        }

        queue = Volley.newRequestQueue(this);

//        setTempHumid();
//        Timer timer = new Timer();
//        TimerTask refresher = new TimerTask() {
//            public void run() {
//                setTempHumid();
//            }
//        };
//        timer.scheduleAtFixedRate(refresher, 0,15000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //make menu item icons white instead of black
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void setTempHumid(){
//        final TextView TX_TH = findViewById(R.id.TX_TH);
////        Log.d("TempHumid", "TempHumid status request: http://192.168.178.200/cgi-bin/tempHumid.py");
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.178.200/cgi-bin/tempHumid.py",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("TempHumid", "response: " + response);
//                        try {
//                            JSONObject jObject = new JSONObject(response);
////                            Log.d("TempHumid", "TempHumid json: " + jObject);
//                            float Temp = Float.valueOf(jObject.getString("Temperature"));
//                            float Humid = Float.valueOf(jObject.getString("Humidity"));
//                            String TempS = String.format(Locale.ENGLISH, "%.01f", Float.valueOf(jObject.getString("Temperature")));
//                            String HumidS = String.format(Locale.ENGLISH, "%.0f", Float.valueOf(jObject.getString("Humidity")));
//                            TX_TH.setText("Temp:\t\t"+ TempS + "°C\nHumid:\t" + HumidS + "%");
//                            Log.d("TempHumid", "Received temp: " + TempS + ", humid: " + HumidS);
//                        } catch (JSONException e) {
//                            Log.d("TempHumid", "Response is not json?");
//                            TX_TH.setText("");
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("TempHumid", "We've got not or error response");
//                error.printStackTrace();
//                TX_TH.setText("");
//            }
//        });
//        queue.add(stringRequest);
////        Log.d("TempHumid", "StringRequest: "+stringRequest);
//
//    }

    public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

        private Boolean mOpenCamera = false;

        public SectionsStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){
                Fragment FragmentFloor1 = Floor1Fragment.newInstance(queue);
                Bundle bundle = new Bundle();
                bundle.putBoolean(OPEN_CAMERA_TAG, mOpenCamera);
                mOpenCamera = false;
                FragmentFloor1.setArguments(bundle);
                return FragmentFloor1;
            }else if(position == 1){
                //return null;
                return Floor2Fragment.newInstance(queue);
            }else if(position == 2){
                //return null;
                return Floor3Fragment.newInstance(queue);
            }else{
                return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }

        public void setOpenCamera(){
            mOpenCamera = true;
            notifyDataSetChanged();
        }
    }
}
