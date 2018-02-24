package nl.nielshokke.huisapp;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;


/**
 * Created by Nelis on 18-2-2018.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String version = "?";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Preference versionPref = findPreference("pref_version");
            versionPref.setSummary(version);

            if(sharedPref.getBoolean("dev_options", false)){
                addDevRemoveOption();
                versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    int counter = 0;
                    public boolean onPreferenceClick(Preference preference) {
                        counter ++;
                        if(counter == 50){
                            Toast.makeText(getActivity(), "Je hebt 50 keer geklikt. Wat wil je hiermee nou berijken?", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
            }else{
                versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    int counter = 0;
                    public boolean onPreferenceClick(Preference preference) {
                        counter ++;
                        if(counter == 10){
                            enableDevSettings();
                        }else if(counter == 50){
                            Toast.makeText(getActivity(), "Je hebt 50 keer geklikt. Wat wil je hiermee nou berijken?", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
            }
        }

        private void enableDevSettings(){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("dev_options", true);
            editor.apply();

            addDevRemoveOption();

            Toast.makeText(getActivity(), "Developer options are enabled", Toast.LENGTH_SHORT).show();
        }

        private void addDevRemoveOption(){
            PreferenceCategory cat_about_app = (PreferenceCategory) findPreference("pref_about_app");

            CheckBoxPreference checkBoxPref = new CheckBoxPreference(getActivity());
            checkBoxPref.setTitle("Developer options");

            checkBoxPref.setChecked(true);
            checkBoxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    CheckBoxPreference checkBoxPref = (CheckBoxPreference) preference;
                    Boolean isChecked = (Boolean) o;
                    if(isChecked){
                        checkBoxPref.setSummary("");
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("dev_options", true);
                        editor.apply();
                    }else{
                        checkBoxPref.setSummary("Go back to main screen to save");
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("dev_options", false);
                        editor.apply();
                    }
                    return true;
                }
            });


            cat_about_app.addPreference(checkBoxPref);
        }
    }
}