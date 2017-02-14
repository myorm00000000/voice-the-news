package com.example.azrie.VoiceTheNews;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Azrie on 22/8/2016.
 */

public class ActivitySettingPreferance extends PreferenceActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    CheckBoxPreference autoPlay = (CheckBoxPreference) findPreference("autoplay");
    CheckBoxPreference checkDownload = (CheckBoxPreference) findPreference("autoDownloadPreference");
    TextToSpeech textToSpeech;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.preferences_activity_settings, root, false);

        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.preferences);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);


        String key = preference.getKey();
        Log.d("Preference ",key);
        // If the user has clicked on a preference screen, set up the screen
        if (preference instanceof PreferenceScreen) {

            if(!key.equals("aboutVTN")){
                setUpNestedScreen((PreferenceScreen) preference, this);
            }

            else if(key.equals("aboutVTN")){
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.preferences_activity_about);
                dialog.show();
                Window window = dialog.getWindow();

                //Percentage calculation for screen percentage
                Point size = new Point();
                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);
                int height = size.y;
                int width = size.x;

                window.setLayout((int) (width * 0.93),(int) (height * 0.35));
            }
        }



        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen, final Context context) {
        final Dialog dialog = preferenceScreen.getDialog();

        Toolbar bar;
        final Preference clearDownload = findPreference("clearDownloadPreference");     //Preference for cleardownload
        final Preference languageTTS = findPreference("TTSLanguage");                   //Preference for TTS language
        final Preference speedRate = findPreference("TTSRate");
        final Preference voicePitch = findPreference("TTSPitch");
        final Preference exampleTTS = findPreference("exampleTTS");
        final Preference languageOption = findPreference("languageOption");
        final Preference clearLogin = findPreference("clearLogin");
        CheckBoxPreference autoPlay = (CheckBoxPreference) findPreference("autoplay");  //Preference for autoplay
        CheckBoxPreference checkDownload = (CheckBoxPreference) findPreference("autoDownloadPreference");  //Preference for auto download
        this.autoPlay = autoPlay;
        this.checkDownload = checkDownload;

        SharedPreferences prefSumd = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String prefSum = null;

        if(prefSumd.getString("sumlanguage",null) != null) {
            prefSum  = prefSumd.getString("sumlanguage", null);
        }

        if(prefSum != null && Objects.equals(prefSum, Locale.getDefault().getDisplayName())){
            Log.d("Check"," Summary Set to Default");
            languageTTS.setSummary("System language");
        }

        else if( prefSum != null && !Objects.equals(prefSum, Locale.getDefault().getDisplayName())){
            Log.d("Check"," Summary Set to " + prefSum);
            languageTTS.setSummary(prefSum);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.preferences_activity_settings, root, false);
            root.addView(bar, 0); // insert at top
        }

        else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.preferences_activity_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setTitle(preferenceScreen.getTitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        languageOption.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                changeLanguageOption();
                return true;
            }
        });

        autoPlay.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });

        checkDownload.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                verifyWritePermission((Activity) context, o);
                return true;
            }
        });

        clearLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor saveEditor = preferences.edit();
                saveEditor.putBoolean("login",false);
                saveEditor.apply();

                Toast.makeText(getApplicationContext(),"Login details cleared", Toast.LENGTH_SHORT).show();

                new WebView(getApplicationContext()).clearCache(true);
                return true;
            }
        });

        clearDownload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteDownloads();
                return true;
            }
        });

        languageTTS.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showRadioButtonDialogLanguage(preference);
                return true;
            }
        });

        speedRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showVoiceSettings("Voice speed rate");
                return true;
            }
        });

        voicePitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showVoiceSettings("Voice pitch level");
                return true;
            }
        });

        exampleTTS.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String languageLocale = preferences.getString("langTest",null);
                String languageDisplayName = preferences.getString("language",null);
                float speed = preferences.getFloat("speed",0);
                float pitch = preferences.getFloat("pitch",0);

                Locale[] locales = Locale.getAvailableLocales();


                for(Locale locale : locales){
                    if(locale.toString().equals(languageLocale)){
                        textToSpeech.setLanguage(locale);
                        Log.d("Check Locale", " getDisLanguage: " + locale.getDisplayLanguage() + " // getLanguage: " + locale.getLanguage() + " // languageLocale: " + languageLocale);
                        textToSpeech.setSpeechRate(speed);
                        textToSpeech.setPitch(pitch);
                        break;
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21("This is an example of speech synthesis in" + languageDisplayName);
                } else {
                    ttsUnder20("This is an example of speech synthesis in" + languageDisplayName);
                }
                //textToSpeech.speak("This is an example of speech synthesis in" + language,TextToSpeech.QUEUE_FLUSH,null);


                return true;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public void changeLanguageOption(){

        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);

        String language = mSharedPreference.getString("language",null);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.preferences_activity_language_option);
        dialog.show();
        Window window = dialog.getWindow();

        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);
        final RadioButton englishRadio = (RadioButton) dialog.findViewById(R.id.radioButtonEn);
        final RadioButton bahasaRadio= (RadioButton) dialog.findViewById(R.id.radioButtonBm);
        RadioButton chineseRadio = (RadioButton) dialog.findViewById(R.id.radioButtonCn);

        radioGroup.clearCheck();

        switch (language) {
            case "EN":
                englishRadio.setChecked(true);
                break;
            case "BM":
                bahasaRadio.setChecked(true);
                break;
            case "CN":
                chineseRadio.setChecked(true);
                break;
        }

        englishRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Please restart the application so that your language changes can take effect", Toast.LENGTH_SHORT).show();
                radioGroup.clearCheck();
                englishRadio.setChecked(true);
                languagePreference("EN");
                dialog.dismiss();
            }
        });

        bahasaRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Please restart the application so that your language changes can take effect", Toast.LENGTH_SHORT).show();
                radioGroup.clearCheck();
                bahasaRadio.setChecked(true);
                languagePreference("BM");
                dialog.dismiss();
            }
        });

        chineseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Please restart the application so that your language changes can take effect", Toast.LENGTH_SHORT).show();
                radioGroup.clearCheck();
                bahasaRadio.setChecked(true);
                languagePreference("CN");
                dialog.dismiss();
            }
        });

        //Percentage calculation for screen percentage
        Point size = new Point();
        Display display = null;
        if (window != null) {
            display = window.getWindowManager().getDefaultDisplay();
        }
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        window.setLayout((int) (width * 0.93),(int) (height * 0.30));

    }

    private void languagePreference(String language){
        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor saveEditor = languagePreference.edit();
        saveEditor.putString("language",language);
        saveEditor.apply();
    }

    public void verifyWritePermission(Activity activity , Object o){

        int hasWritePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean checked = Boolean.valueOf(o.toString());

        if(checked){
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                // prompt the user for permission if no permission granted
                ActivityCompat.requestPermissions(
                        (Activity) activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            } //Output test space
        }
    }

    public void deleteDownloads() {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/voice-the-news/");
        File rootEn = new File(root,"lang-en");
        File rootBm = new File(root,"lang-bm");
        File rootCn = new File(root,"lang-cn");

        //File[] contents = dir.listFiles();
        File[] contentsEn = rootEn.listFiles();
        File[] contentsBm = rootBm.listFiles();
        File[] contentsCn = rootCn.listFiles();

        if(/*contents == null && */contentsEn == null && contentsBm == null && contentsCn == null){
            Toast.makeText(getApplicationContext(), "News folder is empty", Toast.LENGTH_SHORT).show();
        }

        else {
            /*if(contents != null){

                for (File file : dir.listFiles())
                    if (!file.isDirectory())
                        file.delete();
            }*/

            if(contentsEn != null){

                for (File file : rootEn.listFiles())
                    if (!file.isDirectory())
                        file.delete();
            }

            if(contentsBm != null){

                for (File file : rootBm.listFiles())
                    if (!file.isDirectory())
                        file.delete();
            }

            if(contentsCn != null){

                for (File file : rootCn.listFiles())
                    if (!file.isDirectory())
                        file.delete();
            }

            Toast.makeText(getApplicationContext(), "All downloaded news cleared", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showRadioButtonDialogLanguage(final Preference preference){

        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.preferences_activity_language);
        dialog.setTitle("Language");

        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);

        final int[] radioClick = {0};

        Locale[] locales = Locale.getAvailableLocales();

        final List<Locale> localeList = new ArrayList<Locale>();
        
        String localeDefault = Locale.getDefault().getDisplayName();

        for (Locale locale : locales) {

            int res = textToSpeech.isLanguageAvailable(locale);

            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {


                localeList.add(locale);

                Log.d("Check Locale"," DisName: " + locale.getDisplayName() + " // Locale.getLang: " + locale.getLanguage() + " // Locale.getCountry: " + locale.getCountry() + " // Locale: "
                        + locale + " // Locale.getDisLang " + locale.getDisplayLanguage() + " // Locale.getISO3 " + locale.getISO3Language() );

                if(localeDefault.equals(locale.getDisplayName())){

                    Log.d("Check Locale "," Index :" + localeList.indexOf(Locale.getDefault()));
                    Collections.swap(localeList, localeList.indexOf(Locale.getDefault()), 0);

                    localeList.add(locale);

                }
            }
        }

        if(localeList.isEmpty())
            Log.d("Locale List","It's empty");

        List<String> stringList=new ArrayList<>();  // here is list

        for(int i=0;i<30;i++) {
            stringList.add("RadioButton " + (i + 1));
        }

        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);


        int preferencesIntCheck = mSharedPreference1.getInt("rbID",0);
        String preferencesStringCheck = mSharedPreference1.getString("vlanguage",null);

        Log.d("Preference Check ", " " + preferencesIntCheck + " // " + preferencesStringCheck);

        for(int i=0;i<localeList.size();i++){

            final RadioButton rb = new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.

            if( i < 1 )
                rb.setText("Use system language");
            else
                rb.setText(localeList.get(i).getDisplayName());
            //rb.setText(localeList.get(i).toString());

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(800,1);
            params.gravity = Gravity.CENTER;
            View view = new View(this);
            view.setLayoutParams(params);
            view.setBackgroundColor(Color.parseColor("gray"));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rb.setTextAppearance(android.R.style.TextAppearance_Medium);
            }

            else {
                rb.setTextAppearance(this,android.R.style.TextAppearance_Medium);
            }

            final int j = i;

            rb.setPadding(0,40,0,40);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivitySettingPreferance.this, rb.getText()+" Selected ", Toast.LENGTH_SHORT).show();

                    rb.setChecked(true);

                    radioClick[0] = rg.indexOfChild(rb);

                    SharedPreferences setCheckedLang = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor mEdit1 = setCheckedLang.edit();

                    mEdit1.putInt("rbID", rg.getCheckedRadioButtonId());
                    mEdit1.putString("vlanguage",localeList.get(rg.getCheckedRadioButtonId()).getDisplayName());
                    mEdit1.putString("sumlanguage",localeList.get(rg.getCheckedRadioButtonId()).getDisplayName());
                    mEdit1.putString("langTest", String.valueOf(localeList.get(rg.getCheckedRadioButtonId())));
                    mEdit1.apply();

                    Log.d("Click Check : ","" + radioClick[0] + " // ID : " + rg.getCheckedRadioButtonId() + " // Locale : " + localeList.get(rg.getCheckedRadioButtonId()).getDisplayLanguage());

                    dialog.dismiss();

                }
            });

            //rb.setGravity(Gravity.CENTER);
            rb.setId(i);
            rb.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
            rg.addView(rb);

            if( preferencesIntCheck == i){
                Log.d("Click Check : "," IF - " + i + " // ID : " + mSharedPreference1.getInt("radioButtonID",0));
                rb.setChecked(true);
            }

        }



        dialog.show();

        Window window = dialog.getWindow();

        //Percentage calculation for screen percentage
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        window.setLayout((int) (width * 0.95),(int) (height * 0.95));


        final SharedPreferences preferencesSum = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        preferencesSum.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


                String prefSum = null;

                if(preferencesSum.getString("sumlanguage",null) != null) {
                    prefSum  = preferencesSum.getString("sumlanguage", null);
                }

                if(prefSum != null && Objects.equals(prefSum, Locale.getDefault().getDisplayName())){
                    Log.d("Check"," Summary Set to Default");
                    preference.setSummary("System language");
                }

                else if( prefSum != null && !Objects.equals(prefSum, Locale.getDefault().getDisplayName())){
                    Log.d("Check"," Summary Set to " + prefSum);
                    preference.setSummary(prefSum);
                }


            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    public void showVoiceSettings(final String activity){

        final float[] progressVal = new float[1];

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.preferences_activity_voice_slider);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        final TextView rateDisplay = (TextView) dialog.findViewById(R.id.rate);

        final SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seek_bar);

        Button saveButton = (Button) dialog.findViewById(R.id.buttonSave);
        final Button resetButton = (Button) dialog.findViewById(R.id.buttonReset);

        SharedPreferences retrieveSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(activity.equals("Voice speed rate")){

            float seekSavedVal = retrieveSettings.getFloat("speed",0);
            int seekBarVal = (int) (seekSavedVal * 100);

            DecimalFormat df = new DecimalFormat("0.00");
            rateDisplay.setText(df.format(seekSavedVal));

            seekBar.setProgress(seekBarVal);
        }

        else{

            float seekSavedVal = retrieveSettings.getFloat("pitch",0);
            int seekBarVal = (int) (seekSavedVal * 100);

            DecimalFormat df = new DecimalFormat("0.00");
            rateDisplay.setText(df.format(seekSavedVal));

            seekBar.setProgress(seekBarVal);

        }

        //rateDisplay.setText(seekBar.getProgress());

        dialogTitle.setText(activity);

        dialog.show();

        Window window = dialog.getWindow();

        //Percentage calculation for screen percentage
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        window.setLayout((int) (width * 0.95),(int) (height * 0.35));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {

                float progressRate = (float) (progress + 25);
                progressRate = progressRate/100;

                DecimalFormat df = new DecimalFormat("0.00");
                rateDisplay.setText(df.format(progressRate));

                Log.d("Seekbar progress "," " + progress);
                progressVal[0] = progressRate;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settingPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor saveEditor = settingPreference.edit();

                if(activity.equals("Voice speed rate")){
                    saveEditor.putFloat("speed",progressVal[0]);
                    Log.d("Rate Checker "," Speed ");
                    saveEditor.apply();
                    dialog.dismiss();
                }
                else{
                    saveEditor.putFloat("pitch",progressVal[0]);
                    Log.d("Rate Checker "," Pitch ");
                    saveEditor.apply();
                    dialog.dismiss();
                }

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rateDisplay.setText("1.00");
                seekBar.setProgress(75);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Log.d("Permission Denied: "," TEST ");
                this.checkDownload.setChecked(false);
            }
        }
    }
}
