package de.teamescape.dev.teamescapefbcounter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static SharedPreferences settings;
    public static Activity activity;
    public static final String TEFBC_NAME = "TEFBC_PREFS";
    public static boolean inSettings = false;

    public FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
    public static Context backgroundfragmentcontext;
    public static Context logofragmentcontext;
    public static Context facebookfragmentcontext;
    public static Context qrcodefragmentcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupActionBar();
        activity = this;
        settings = activity.getSharedPreferences(TEFBC_NAME, Context.MODE_PRIVATE);
        fragmentManager = this.getFragmentManager();
        fragmentTransaction = this.fragmentManager.beginTransaction();

        fragmentTransaction.addToBackStack("header");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(onIsMultiPane()){
            NavUtils.navigateUpFromSameTask(this);
            inSettings = false;
            activity.finish();
        }else{
            if (id == android.R.id.home && inSettings) {
                startActivity(new Intent(activity, SettingsActivity.class));
                inSettings = false;
                return true;
            }else{
                NavUtils.navigateUpFromSameTask(this);
                activity.finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || BackgroundImagePreferenceFragment.class.getName().equals(fragmentName)
                || LogoImagePreferenceFragment.class.getName().equals(fragmentName)
                || FacebookPreferenceFragment.class.getName().equals(fragmentName)
                || CounterPreferenceFragment.class.getName().equals(fragmentName)
                || TextPreferenceFragment.class.getName().equals(fragmentName)
                || QRPreferenceFragment.class.getName().equals(fragmentName)
                || LogPreferenceFragment.class.getName().equals(fragmentName);
    }

    public static void setDefault(Preference pref, String pref_type) {

        switch(pref_type){
            case "EditTextPreference":
                EditTextPreference temp_text = (EditTextPreference)pref;
                temp_text.setText(settings.getString(pref.getKey(), null));
                Log.d(TAG, "shared preference " + pref.getKey() + " init with " + settings.getString(pref.getKey(), null));
                //Toast.makeText(activity, pref.getKey() + " was set to " + settings.getString(pref.getKey(), null), Toast.LENGTH_SHORT).show();
                break;
            case "SwitchPreference":
                SwitchPreference temp_switch = (SwitchPreference)pref;
                temp_switch.setChecked(Boolean.parseBoolean(settings.getString(pref.getKey(),null)));
                Log.d(TAG, "shared preference " + pref.getKey() + " init with " + settings.getString(pref.getKey(), null));
                break;
            default:
        }
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(TEFBC_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    private static void save(String key, Object newValue) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, newValue.toString());
        editor.commit();
    }

    public static void removeValue(Context context, String TEFBC_KEY) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(TEFBC_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(TEFBC_KEY);
        editor.commit();
    }

    public static boolean checkmarginsizeinput(Object newValue, Double minPort, Double maxPort){
        try{
            Double val = Double.valueOf(newValue.toString());
            if ((val > minPort) && (val < maxPort)) {
                return true;
            }else{
                Toast.makeText(activity, "invalid input", Toast.LENGTH_LONG).show();
                return false;
            }
        }catch(Exception e){
            Toast.makeText(activity, "invalid input", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static void setListPreferenceImageList(ListPreference lp, String PrefTag) {

        String path = "/data/data/de.teamescape.dev.teamescapefbcounter/app_imageDir/"+PrefTag;
        File f = new File(path);
        File file[] = f.listFiles();
        String[] imgEntries;
        String[] imgValues;

        if(file!=null){
            imgEntries = new String[file.length+1];
            imgValues = new String[file.length+1];
            for (int i=1; i < file.length+1; i++)
            {
                imgEntries[i] = file[i-1].getName();
                imgValues[i]=file[i-1].getName();
            }
        }else{
            imgEntries = new String[1];
            imgValues = new String[1];
        }
        int index = -1;
        switch (PrefTag){
                case "BACKGROUNDIMAGEURI":
                    imgEntries[0] = "background_init";
                    imgValues[0] = "background_init";
                    index = findDefaultValueIndex(settings.getString("BACKGROUNDIMAGETITLE",null),imgValues);
                    break;
                case "LOGOIMAGEURI":
                    imgEntries[0] = "logo_init";
                    imgValues[0] = "logo_init";
                    index = findDefaultValueIndex(settings.getString("LOGOIMAGETITLE",null),imgValues);
                    break;
                case "FACEBOOKIMAGEURI":
                    imgEntries[0] = "facebook_thumb_init";
                    imgValues[0] = "facebook_thumb_init";
                    index = findDefaultValueIndex(settings.getString("FACEBOOKIMAGETITLE",null),imgValues);
                    break;
                case "QRCODEIMAGEURI":
                    imgEntries[0] = "qrcode_init";
                    imgValues[0] = "qrcode_init";
                    index = findDefaultValueIndex(settings.getString("QRCODEIMAGETITLE",null),imgValues);
                    break;
                default:
            }

        lp.setEntries(imgEntries);
        lp.setEntryValues(imgValues);
        lp.setValueIndex(index);
    }

    private static int findDefaultValueIndex(String backgroundimagetitle, String[] imgValues) {

        String defaultValue = backgroundimagetitle;
        int index = -1;
        for (int i=0;i<imgValues.length;i++) {
            if (imgValues[i].equals(defaultValue)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @SuppressLint("ValidFragment")
    public static class BackgroundImagePreferenceFragment extends PreferenceFragment {
        final int PICK_IMAGE_REQUEST = 1;
        final int PICK_IMAGE_REQUEST_KITKAT=2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_background);
            inSettings = true;
            setHasOptionsMenu(true);
            backgroundfragmentcontext = getActivity();

            /*
            EditTextPreference bg_img_title = (EditTextPreference) findPreference("BACKGROUNDIMAGETITLE");
            setDefault(bg_img_title, "EditTextPreference");

            bg_img_title.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    EditTextPreference bg_img_title = (EditTextPreference) findPreference("BACKGROUNDIMAGETITLE");
                    if(checktitleinput(String.valueOf(newValue),"BACKGROUNDIMAGEINTERNALURL")) {
                        save(preference.getKey(), newValue);
                        setDefault(bg_img_title, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });
            */

            final ListPreference bg_img_listPreference = (ListPreference) findPreference("BACKGROUNDIMAGETITLELIST");

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceImageList(bg_img_listPreference, "BACKGROUNDIMAGEURI");

            bg_img_listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference bg_img_title = (EditTextPreference) findPreference("BACKGROUNDIMAGETITLE");
                    int index = bg_img_listPreference.findIndexOfValue(newValue.toString());
                    Toast.makeText(activity.getBaseContext(), "Background set to: " + bg_img_listPreference.getEntries()[index], Toast.LENGTH_LONG).show();
                    save(bg_img_title.getKey(), bg_img_listPreference.getEntries()[index]);
                    setDefault(bg_img_title, "EditTextPreference");
                    return true;
                }
            });

            Preference bg_img_load = (Preference) findPreference("BACKGROUNDIMAGEURI");
            //setDefault(bg_img_load, "EditTextPreference");


            bg_img_load.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                        //go on when permission was given -> onRequestPermissionsResult
                    } else {
                        if (Build.VERSION.SDK_INT < 19) {
                            Intent intent = new Intent();
                            intent.setType("image*//*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                    }
                    return true;
                }
            });

            Preference bg_restore = (Preference) findPreference("restore_background");

            bg_restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Background reset")
                            .setMessage("Do you really want to set the background to default?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeValue(activity, "BACKGROUNDIMAGEURI");
                                    removeValue(activity, "BACKGROUNDIMAGEINTERNALURL");
                                    save("BACKGROUNDIMAGETITLE","background_init");
                                    Toast.makeText(getActivity(), "background was set to default setting", Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }


        @Override
        public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 2909: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT <19){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                        Log.d(TAG, "Write Storage Granted. Storage writable="+ grantResults[0]);

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'switch' lines to check for other
                // permissions this app might request
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode != Activity.RESULT_OK) return;
                if (null == data) return;
                Uri originalUri = null;

                if (requestCode == PICK_IMAGE_REQUEST) {
                    originalUri = data.getData();
                } else if (requestCode == PICK_IMAGE_REQUEST_KITKAT) {
                    originalUri = data.getData();
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION) ;
                    // Check for the freshest data.
                    //noinspection ResourceType
                    getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);

                }

                save("BACKGROUNDIMAGEURI",originalUri);

                Log.d(TAG, "originalURI was saved: " + originalUri);


            }
    }

    public static class LogoImagePreferenceFragment extends PreferenceFragment {
        final int PICK_IMAGE_REQUEST = 1;
        final int PICK_IMAGE_REQUEST_KITKAT=2;
        final Double minPort = 0.0;
        final Double maxPort = 1.0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_logo);
            setHasOptionsMenu(true);
            logofragmentcontext = getActivity();
            inSettings = true;

            /*
            EditTextPreference logo_img_title = (EditTextPreference)findPreference("LOGOIMAGETITLE");
            setDefault(logo_img_title, "EditTextPreference");

            logo_img_title.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    EditTextPreference logo_img_title = (EditTextPreference) findPreference("LOGOIMAGETITLE");
                    if (checktitleinput(String.valueOf(newValue), "LOGOIMAGEINTERNALURL")) {
                        save(preference.getKey(), newValue);
                        setDefault(logo_img_title, "EditTextPreference");
                        return true;
                    } else {
                        return false;
                    }
                }

            });
            */

            final ListPreference logo_img_listPreference = (ListPreference) findPreference("LOGOIMAGETITLELIST");

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceImageList(logo_img_listPreference, "LOGOIMAGEURI");

            logo_img_listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference logo_img_title = (EditTextPreference) findPreference("LOGOIMAGETITLE");
                    int index = logo_img_listPreference.findIndexOfValue(newValue.toString());
                    Toast.makeText(activity.getBaseContext(), "Logo set to: " + logo_img_listPreference.getEntries()[index], Toast.LENGTH_LONG).show();
                    save(logo_img_title.getKey(), logo_img_listPreference.getEntries()[index]);
                    setDefault(logo_img_title, "EditTextPreference");
                    return true;
                }
            });

            EditTextPreference logo_img_sizeratio = (EditTextPreference)findPreference("LOGOIMAGESIZERATIO");
            setDefault(logo_img_sizeratio, "EditTextPreference");

            logo_img_sizeratio.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference logo_img_ratio = (EditTextPreference) findPreference("LOGOIMAGESIZERATIO");
                        setDefault(logo_img_ratio, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }
            });

            EditTextPreference logo_img_left_margin = (EditTextPreference)findPreference("LOGOIMAGELEFTMARGIN");
            //setDefault(logo_img_left_margin, "EditTextPreference");

            logo_img_left_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference logo_img_left_margin = (EditTextPreference) findPreference("LOGOIMAGELEFTMARGIN");
                        setDefault(logo_img_left_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }
            });

            EditTextPreference logo_img_top_margin = (EditTextPreference)findPreference("LOGOIMAGETOPMARGIN");
            setDefault(logo_img_top_margin, "EditTextPreference");

            logo_img_top_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference logo_img_top_margin = (EditTextPreference) findPreference("LOGOIMAGETOPMARGIN");
                        setDefault(logo_img_top_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }
            });

            Preference logo_img_load = (Preference) findPreference("LOGOIMAGEURI");
            //setDefault(bg_img_load, "EditTextPreference");

            logo_img_load.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                        //go on when permission was given -> onRequestPermissionsResult
                    } else {
                        if (Build.VERSION.SDK_INT < 19) {
                            Intent intent = new Intent();
                            intent.setType("image*//*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                    }
                    return true;
                }
            });

            Preference logo_restore = (Preference) findPreference("restore_logo_image");

            logo_restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Logo image reset")
                            .setMessage("Do you really want to set the logo image to default?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeValue(activity, "LOGOIMAGEURI");
                                    removeValue(activity, "LOGOIMAGEINTERNALURL");
                                    save("LOGOIMAGETITLE", "logo_init");
                                    save("LOGOIMAGESIZERATIO", "0.4");
                                    save("LOGOIMAGELEFTMARGIN", "0.5");
                                    save("LOGOIMAGETOPMARGIN", "0.01");
                                    Toast.makeText(getActivity(), "logo image was set to default setting", Toast.LENGTH_SHORT).show();

                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 2909: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT <19){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                        Log.d(TAG, "Write Storage Granted. Storage writable="+ grantResults[0]);

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'switch' lines to check for other
                // permissions this app might request
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != Activity.RESULT_OK) return;
            if (null == data) return;
            Uri originalUri = null;

            if (requestCode == PICK_IMAGE_REQUEST) {
                originalUri = data.getData();
            } else if (requestCode == PICK_IMAGE_REQUEST_KITKAT) {
                originalUri = data.getData();
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION) ;
                // Check for the freshest data.
                //noinspection ResourceType
                getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);

            }

            save("LOGOIMAGEURI",originalUri);

            Log.d(TAG, "originalURI was saved: " + originalUri);


        }
    }

    public static class FacebookPreferenceFragment extends PreferenceFragment {
        final int PICK_IMAGE_REQUEST = 1;
        final int PICK_IMAGE_REQUEST_KITKAT=2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_faceook);
            setHasOptionsMenu(true);
            facebookfragmentcontext = getActivity();
            inSettings = true;
            /*
            EditTextPreference facebook_img_title = (EditTextPreference)findPreference("FACEBOOKIMAGETITLE");
            setDefault(facebook_img_title, "EditTextPreference");

            facebook_img_title.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    EditTextPreference facebook_img_title = (EditTextPreference) findPreference("FACEBOOKIMAGETITLE");
                    if(checktitleinput(String.valueOf(newValue), "FACEBOOKIMAGEINTERNALURL")) {
                        save(preference.getKey(), newValue);
                        setDefault(facebook_img_title, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }

                }

            });
            */

            final ListPreference fb_img_listPreference = (ListPreference) findPreference("FACEBOOKIMAGETITLELIST");

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceImageList(fb_img_listPreference, "FACEBOOKIMAGEURI");

            fb_img_listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference fb_img_title = (EditTextPreference) findPreference("FACEBOOKIMAGETITLE");
                    int index = fb_img_listPreference.findIndexOfValue(newValue.toString());
                    Toast.makeText(activity.getBaseContext(), "Facebook icon set to: " + fb_img_listPreference.getEntries()[index], Toast.LENGTH_LONG).show();
                    save(fb_img_title.getKey(), fb_img_listPreference.getEntries()[index]);
                    setDefault(fb_img_title, "EditTextPreference");
                    return true;
                }
            });

            EditTextPreference facebook_img_sizeratio = (EditTextPreference)findPreference("FACEBOOKIMAGESIZERATIO");
            setDefault(facebook_img_sizeratio, "EditTextPreference");

            facebook_img_sizeratio.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue, 0.0, 1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference facebook_img_sizeratio = (EditTextPreference) findPreference("FACEBOOKIMAGESIZERATIO");
                        setDefault(facebook_img_sizeratio, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference facebook_img_left_margin = (EditTextPreference)findPreference("FACEBOOKIMAGELEFTMARGIN");
            setDefault(facebook_img_left_margin, "EditTextPreference");

            facebook_img_left_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference facebook_img_left_margin = (EditTextPreference) findPreference("FACEBOOKIMAGELEFTMARGIN");
                        setDefault(facebook_img_left_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference facebook_img_top_margin = (EditTextPreference)findPreference("FACEBOOKIMAGETOPMARGIN");
            setDefault(facebook_img_top_margin, "EditTextPreference");

            facebook_img_top_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    if (checkmarginsizeinput(newValue, 0.0, 1.0)) {
                        save(preference.getKey(), newValue);
                        EditTextPreference facebook_img_top_margin = (EditTextPreference) findPreference("FACEBOOKIMAGETOPMARGIN");
                        setDefault(facebook_img_top_margin, "EditTextPreference");
                        return true;
                    } else {
                        return false;
                    }
                }

            });

            EditTextPreference facebook_fwl_call_url = (EditTextPreference)findPreference("FACEBOOKFQLCALLURL");
            setDefault(facebook_fwl_call_url, "EditTextPreference");

            facebook_fwl_call_url.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    save(preference.getKey(), newValue);
                    EditTextPreference facebook_fwl_call_url = (EditTextPreference) findPreference("FACEBOOKFQLCALLURL");
                    setDefault(facebook_fwl_call_url, "EditTextPreference");
                    return true;
                }

            });

            EditTextPreference facebook_last_known_likes = (EditTextPreference)findPreference("LASTKNOWNFACEBOOKCOUNT");
            setDefault(facebook_last_known_likes, "EditTextPreference");

            facebook_last_known_likes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    save(preference.getKey(), newValue);
                    EditTextPreference facebook_last_known_likes = (EditTextPreference) findPreference("LASTKNOWNFACEBOOKCOUNT");
                    setDefault(facebook_last_known_likes, "EditTextPreference");
                    return true;
                }

            });

            SwitchPreference facebook_fixed_allocation_counter_fb = (SwitchPreference) findPreference("FIXEDALLOCATIONCOUNTERFB");
            setDefault(facebook_fixed_allocation_counter_fb, "SwitchPreference");
            facebook_fixed_allocation_counter_fb.setChecked(Boolean.parseBoolean(settings.getString(facebook_fixed_allocation_counter_fb.getKey(), null)));
            if(facebook_fixed_allocation_counter_fb.isChecked()){
                facebook_img_top_margin.setEnabled(false);
                facebook_img_left_margin.setEnabled(false);
                facebook_img_sizeratio.setEnabled(false);
            }

            facebook_fixed_allocation_counter_fb.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();
                    save(preference.getKey(), !switched);
                    SwitchPreference facebook_fixed_allocation_counter_fb = (SwitchPreference) findPreference("FIXEDALLOCATIONCOUNTERFB");
                    setDefault(facebook_fixed_allocation_counter_fb, "SwitchPreference");
                    EditTextPreference facebook_img_top_margin = (EditTextPreference) findPreference("FACEBOOKIMAGETOPMARGIN");
                    EditTextPreference facebook_img_left_margin = (EditTextPreference) findPreference("FACEBOOKIMAGELEFTMARGIN");
                    EditTextPreference facebook_img_sizeratio = (EditTextPreference) findPreference("FACEBOOKIMAGESIZERATIO");
                    if (!switched) {
                        facebook_img_top_margin.setEnabled(false);
                        facebook_img_left_margin.setEnabled(false);
                        facebook_img_sizeratio.setEnabled(false);
                    } else {
                        facebook_img_top_margin.setEnabled(true);
                        facebook_img_left_margin.setEnabled(true);
                        facebook_img_sizeratio.setEnabled(true);
                    }
                    return !switched;
                }
            });

            Preference facebook_img_load = (Preference) findPreference("FACEBOOKIMAGEURI");
            //setDefault(bg_img_load, "EditTextPreference");

            facebook_img_load.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                        //go on when permission was given -> onRequestPermissionsResult
                    } else {
                        if (Build.VERSION.SDK_INT < 19) {
                            Intent intent = new Intent();
                            intent.setType("image*//*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                    }
                    return true;
                }
            });

            Preference fb_restore = (Preference) findPreference("restore_facebook_image");

            fb_restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Facebook image reset")
                            .setMessage("Do you really want to set the facebook image to default?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeValue(activity, "FACEBOOKIMAGEURI");
                                    removeValue(activity, "FACEBOOKIMAGEINTERNALURL");
                                    save("FACEBOOKIMAGETITLE", "facebook_thumb_init");
                                    save("FACEBOOKIMAGESIZERATIO", "0.10");
                                    save("FACEBOOKIMAGELEFTMARGIN", "0.25");
                                    save("FACEBOOKIMAGETOPMARGIN", "0.45");
                                    Toast.makeText(getActivity(), "facebook image was set to default setting", Toast.LENGTH_SHORT).show();

                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });

        }

        @Override
        public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 2909: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT <19){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                        Log.d(TAG, "Write Storage Granted. Storage writable="+ grantResults[0]);

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'switch' lines to check for other
                // permissions this app might request
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != Activity.RESULT_OK) return;
            if (null == data) return;
            Uri originalUri = null;

            if (requestCode == PICK_IMAGE_REQUEST) {
                originalUri = data.getData();
            } else if (requestCode == PICK_IMAGE_REQUEST_KITKAT) {
                originalUri = data.getData();
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION) ;
                // Check for the freshest data.
                //noinspection ResourceType
                getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);

            }

            save("FACEBOOKIMAGEURI",originalUri);

            Log.d(TAG, "originalURI was saved: " + originalUri);


        }

    }

    public static class CounterPreferenceFragment extends PreferenceFragment{
        final int PICK_IMAGE_REQUEST = 1;
        final int PICK_IMAGE_REQUEST_KITKAT=2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_counter);
            setHasOptionsMenu(true);
            inSettings = true;

            SwitchPreference counter_animation = (SwitchPreference) findPreference("COUNTERANIMATION");
            counter_animation.setChecked(Boolean.parseBoolean(settings.getString(counter_animation.getKey(), null)));
            counter_animation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    save(preference.getKey(), newValue);
                    return true;
                }

            });




            EditTextPreference counter_text_size = (EditTextPreference)findPreference("COUNTERTEXTSIZE");
            setDefault(counter_text_size, "EditTextPreference");

            counter_text_size.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    if(checkmarginsizeinput(newValue,0.0,999.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference counter_text_size = (EditTextPreference) findPreference("COUNTERTEXTSIZE");
                        setDefault(counter_text_size, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference counter_text_left_margin = (EditTextPreference)findPreference("COUNTERTEXTLEFTMARGIN");
            setDefault(counter_text_left_margin, "EditTextPreference");

            counter_text_left_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if (checkmarginsizeinput(newValue, 0.0, 1.0)) {
                        save(preference.getKey(), newValue);
                        EditTextPreference counter_text_left_margin = (EditTextPreference) findPreference("COUNTERTEXTLEFTMARGIN");
                        setDefault(counter_text_left_margin, "EditTextPreference");
                        return true;
                    } else {
                        return false;
                    }
                }

            });

            EditTextPreference counter_text_top_margin = (EditTextPreference)findPreference("COUNTERTEXTTOPMARGIN");
            setDefault(counter_text_top_margin, "EditTextPreference");

            counter_text_top_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if (checkmarginsizeinput(newValue, 0.0, 1.0)) {
                        save(preference.getKey(), newValue);
                        EditTextPreference counter_text_top_margin = (EditTextPreference) findPreference("COUNTERTEXTTOPMARGIN");
                        setDefault(counter_text_top_margin, "EditTextPreference");
                        return true;
                    } else {
                        return false;
                    }
                }

            });
        }
    }

    public static class TextPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_text);
            setHasOptionsMenu(true);
            inSettings = true;

            EditTextPreference text_center_content = (EditTextPreference)findPreference("TEXTCONTENTCENTER");
            setDefault(text_center_content, "EditTextPreference");

            text_center_content.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    save(preference.getKey(), newValue);
                    EditTextPreference text_center_content = (EditTextPreference) findPreference("TEXTCONTENTCENTER");
                    setDefault(text_center_content, "EditTextPreference");
                    return true;
                }

            });

            EditTextPreference text_center_content_left_margin = (EditTextPreference)findPreference("TEXTCONTENTCENTERLEFTMARGIN");
            setDefault(text_center_content_left_margin, "EditTextPreference");

            text_center_content_left_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference text_center_content_left_margin = (EditTextPreference) findPreference("TEXTCONTENTCENTERLEFTMARGIN");
                        setDefault(text_center_content_left_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference text_center_content_top_margin = (EditTextPreference)findPreference("TEXTCONTENTCENTERTOPMARGIN");
            setDefault(text_center_content_top_margin, "EditTextPreference");

            text_center_content_top_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference text_center_content_top_margin = (EditTextPreference) findPreference("TEXTCONTENTCENTERTOPMARGIN");
                        setDefault(text_center_content_top_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference text_center_content_font_size = (EditTextPreference)findPreference("TEXTCONTENTCENTERFONTSIZE");
            setDefault(text_center_content_font_size, "EditTextPreference");

            text_center_content_font_size.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,999.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference text_center_content_font_size = (EditTextPreference) findPreference("TEXTCONTENTCENTERFONTSIZE");
                        setDefault(text_center_content_font_size, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference text_buttom_content = (EditTextPreference)findPreference("TEXTCONTENTBUTTOM");
            setDefault(text_buttom_content, "EditTextPreference");

            text_buttom_content.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    save(preference.getKey(), newValue);
                    EditTextPreference text_buttom_content = (EditTextPreference) findPreference("TEXTCONTENTBUTTOM");
                    setDefault(text_buttom_content, "EditTextPreference");
                    return true;
                }

            });

            EditTextPreference text_buttom_left_margin = (EditTextPreference)findPreference("TEXTCONTENTBUTTOMLEFTMARGIN");
            setDefault(text_buttom_left_margin, "EditTextPreference");

            text_buttom_left_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference text_buttom_left_margin = (EditTextPreference) findPreference("TEXTCONTENTBUTTOMLEFTMARGIN");
                        setDefault(text_buttom_left_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference text_buttom_top_margin = (EditTextPreference)findPreference("TEXTCONTENTBUTTOMTOPMARGIN");
            setDefault(text_buttom_top_margin, "EditTextPreference");

            text_buttom_top_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference text_buttom_top_margin = (EditTextPreference) findPreference("TEXTCONTENTBUTTOMTOPMARGIN");
                        setDefault(text_buttom_top_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference text_buttom_font_size = (EditTextPreference)findPreference("TEXTCONTENTBUTTOMFONTSIZE");
            setDefault(text_buttom_font_size, "EditTextPreference");

            text_buttom_font_size.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,999.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference text_buttom_font_size = (EditTextPreference) findPreference("TEXTCONTENTBUTTOMFONTSIZE");
                        setDefault(text_buttom_font_size, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            SwitchPreference text_content_as_hyperlink = (SwitchPreference) findPreference("TEXTCONTENTBUTTOMHYPERLINK");
            setDefault(text_content_as_hyperlink, "SwitchPreference");

            text_content_as_hyperlink.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference)
                            .isChecked();
                    save(preference.getKey(), !switched);
                    SwitchPreference text_content_as_hyperlink = (SwitchPreference) findPreference("TEXTCONTENTBUTTOMHYPERLINK");
                    setDefault(text_content_as_hyperlink, "SwitchPreference");
                    return !switched;
                }
            });

            Preference text_restore = (Preference) findPreference("restore_text_settings");

            text_restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Text reset")
                            .setMessage("Do you really want to set the background to default?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    save("TEXTCONTENTCENTER", "Hat es euch gefallen? \n Dann einfach direkt auf Facebook liken!");
                                    save("TEXTCONTENTCENTERLEFTMARGIN", "0.18");
                                    save("TEXTCONTENTCENTERTOPMARGIN", "0.7");
                                    save("TEXTCONTENTCENTERFONTSIZE", "20");
                                    save("TEXTCONTENTBUTTOM", "www.facebook.com/TeamEscapeDE");
                                    save("TEXTCONTENTBUTTOMLEFTMARGIN", "0.22");
                                    save("TEXTCONTENTBUTTOMTOPMARGIN", "0.9");
                                    save("TEXTCONTENTBUTTOMFONTSIZE", "20");
                                    save("TEXTCONTENTBUTTOMHYPERLINK", "false");
                                    Toast.makeText(getActivity(), "text was set to default setting", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });

        }


    }

    public static class QRPreferenceFragment extends PreferenceFragment {
        final int PICK_IMAGE_REQUEST = 1;
        final int PICK_IMAGE_REQUEST_KITKAT=2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_qr);
            setHasOptionsMenu(true);
            qrcodefragmentcontext = getActivity();
            inSettings = true;
            /*
            EditTextPreference qrcode_img_title = (EditTextPreference)findPreference("QRCODEIMAGETITLE");
            setDefault(qrcode_img_title, "EditTextPreference");

            qrcode_img_title.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    EditTextPreference qrcode_img_title = (EditTextPreference) findPreference("QRCODEIMAGETITLE");
                    if(checktitleinput(String.valueOf(newValue),"QRCODEIMAGEINTERNALURL")) {
                        save(preference.getKey(), newValue);
                        setDefault(qrcode_img_title, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });
            */

            final ListPreference qrcode_img_listPreference = (ListPreference) findPreference("QRCODEIMAGETITLELIST");

            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceImageList(qrcode_img_listPreference, "QRCODEIMAGEURI");

            qrcode_img_listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference qrcode_img_title = (EditTextPreference) findPreference("QRCODEIMAGETITLE");
                    int index = qrcode_img_listPreference.findIndexOfValue(newValue.toString());
                    Toast.makeText(activity.getBaseContext(), "QR-Code image set to: " + qrcode_img_listPreference.getEntries()[index], Toast.LENGTH_LONG).show();
                    save(qrcode_img_title.getKey(), qrcode_img_listPreference.getEntries()[index]);
                    setDefault(qrcode_img_title, "EditTextPreference");
                    return true;
                }
            });

            EditTextPreference qrcode_img_sizeratio = (EditTextPreference)findPreference("QRCODEIMAGESIZERATIO");
            setDefault(qrcode_img_sizeratio, "EditTextPreference");

            qrcode_img_sizeratio.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference facebook_img_sizeratio = (EditTextPreference) findPreference("QRCODEIMAGESIZERATIO");
                        setDefault(facebook_img_sizeratio, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference qrcode_img_left_margin = (EditTextPreference)findPreference("QRCODEIMAGELEFTMARGIN");
            setDefault(qrcode_img_left_margin, "EditTextPreference");

            qrcode_img_left_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference qrcode_img_left_margin = (EditTextPreference) findPreference("QRCODEIMAGELEFTMARGIN");
                        setDefault(qrcode_img_left_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            EditTextPreference qrcode_img_top_margin = (EditTextPreference)findPreference("QRCODEIMAGETOPMARGIN");
            setDefault(qrcode_img_top_margin, "EditTextPreference");

            qrcode_img_top_margin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    if(checkmarginsizeinput(newValue,0.0,1.0)){
                        save(preference.getKey(), newValue);
                        EditTextPreference qrcode_img_top_margin = (EditTextPreference) findPreference("QRCODEIMAGETOPMARGIN");
                        setDefault(qrcode_img_top_margin, "EditTextPreference");
                        return true;
                    }else{
                        return false;
                    }
                }

            });

            Preference qrcode_img_load = (Preference) findPreference("QRCODEIMAGEURI");

            qrcode_img_load.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                        //go on when permission was given -> onRequestPermissionsResult
                    } else {
                        if (Build.VERSION.SDK_INT < 19) {
                            Intent intent = new Intent();
                            intent.setType("image*//*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                    }
                    return true;
                }
            });

            Preference qrcode_restore = (Preference) findPreference("restore_qrcode_image");

            qrcode_restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("QR code image reset")
                            .setMessage("Do you really want to set the QR code image to default?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeValue(activity, "QRCODEIMAGEURI");
                                    removeValue(activity, "QRCODEIMAGEINTERNALURL");
                                    save("QRCODEIMAGETITLE", "qrcode_init");
                                    save("QRCODEIMAGESIZERATIO", "0.1");
                                    save("QRCODEIMAGELEFTMARGIN", "0.85");
                                    save("QRCODEIMAGETOPMARGIN", "0.8");
                                    Toast.makeText(getActivity(), "logo image was set to default setting", Toast.LENGTH_SHORT).show();

                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 2909: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT <19){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST_KITKAT);
                        }
                        Log.d(TAG, "Write Storage Granted. Storage writable="+ grantResults[0]);

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'switch' lines to check for other
                // permissions this app might request
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != Activity.RESULT_OK) return;
            if (null == data) return;
            Uri originalUri = null;

            if (requestCode == PICK_IMAGE_REQUEST) {
                originalUri = data.getData();
            } else if (requestCode == PICK_IMAGE_REQUEST_KITKAT) {
                originalUri = data.getData();
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION) ;
                // Check for the freshest data.
                //noinspection ResourceType
                getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);

            }
            save("QRCODEIMAGEURI", originalUri);
            Log.d(TAG, "originalURI was saved: " + originalUri);
        }

    }

    public static class LogPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_log);
            setHasOptionsMenu(true);
            inSettings = true;

            Preference log = (Preference) findPreference("LOG");

            log.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent("de.teamescape.dev.teamescapefbcounter.utils.LogActivity");
                    startActivity(i);
                    return true;
                }

            });

            Preference first_run = (Preference) findPreference("first_run");

            first_run.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Wipe shared preferences")
                            .setMessage("Do you really want to set the app preferences to default?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    clearSharedPreference(activity);
                                    //removeValue(activity,"FIRSTRUN");
                                    Toast.makeText(getActivity(), "app settings were set to default setting", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });

            Preference wipe_cache = (Preference) findPreference("wipe_cache");

            wipe_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Wipe app cache")
                            .setMessage("If you clear the cache the app will use default settings and all stored images are deleted. The app will continue in the start screen")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deleteCache(activity);
                                    clearSharedPreference(activity);
                                    Toast.makeText(getActivity(), "app cache cleared & init with defaults & close settings", Toast.LENGTH_SHORT).show();
                                    NavUtils.navigateUpFromSameTask(activity);
                                    activity.finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }

        private void deleteCache(Context context) {
            try {
                File dir = context.getCacheDir();
                deleteDir(dir);
                File imgdir = new File("/data/data/de.teamescape.dev.teamescapefbcounter/app_imageDir");
                deleteDir(imgdir);
            } catch (Exception e) {}
        }

        public static boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
                return dir.delete();
            }
            else if(dir!= null && dir.isFile())
                return dir.delete();
            else {
                return false;
            }
        }
    }

}
