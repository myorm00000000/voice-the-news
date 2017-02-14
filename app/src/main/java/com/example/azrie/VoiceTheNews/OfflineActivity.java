package com.example.azrie.VoiceTheNews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Azrie on 02/9/2016.
 */
public class OfflineActivity extends Activity implements View.OnClickListener {
    private View buttonPrevious, buttonRewind, buttonPlay_Pause, buttonStop, buttonForward, buttonNext;  //Initialize buttons inside toolbar
    private TextToSpeech textToSpeech;   // Initialize TextToSpeech

    private boolean stopControl = false;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean kitkatStopControl = false;
    private boolean kitkatPreviousControl = false;
    private boolean lollipopPreviousControl = false;
    private boolean lollipopForwardControl = false;
    private boolean lollipopStopControl = false;
    private boolean lollipopStartTTS = false;
    private boolean lollipopNextControl = false;
    private boolean lollipopFinishReadCheck = false;
    private boolean kitKatNextControl = false;
    private boolean kitkatFinishReadCheck = false;
    private boolean lollipopNextClicked = false;
    private boolean lollipopPreviousClicked = false;

    private int trackParagraph = 0;
    private int layoutPosition;
    private String language;

    private ArrayList<DataHTML> fileContents;
    private ArrayList<String> paragraph = new ArrayList<>();
    private Bundle paramsLollipop = new Bundle();//For InitListener
    private HashMap<String, String> paramsKitkat = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offline_reader);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout);
        TextView title_text_o = (TextView) findViewById(R.id.title_text_o);

        buttonPrevious = findViewById(R.id.zero);
        buttonRewind = findViewById(R.id.one);
        buttonPlay_Pause = findViewById(R.id.two);
        buttonStop = findViewById(R.id.three);
        buttonForward = findViewById(R.id.four);
        buttonNext = findViewById(R.id.five);

        buttonPrevious.setOnClickListener(this);
        buttonRewind.setOnClickListener(this);
        buttonPlay_Pause.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonForward.setOnClickListener(this);
        buttonNext.setOnClickListener(this);


        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(this);
        language = languagePreference.getString("language","DEFAULT");

        Bundle extras = getIntent().getBundleExtra("bundle");

        if(extras != null) {
            fileContents = extras.getParcelableArrayList("link");
            layoutPosition = extras.getInt("position");
            paragraph.add(fileContents.get(layoutPosition).getTitle());

        }

        title_text_o.setText(paragraph.get(0));

        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        getFileName();

        for(int i = 1; i < paragraph.size(); i++){
            TextView tv= new TextView(this);
            tv.setPadding(50,25,50,25);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            tv.setLayoutParams(lparams);
            tv.setText(paragraph.get(i));
            layout.addView(tv);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {       // TextToSpeech listener

                if(status != TextToSpeech.ERROR) {                                  // If no error, set TTS language
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String language = preferences.getString("vlanguage",null);
                    float speed = preferences.getFloat("speed",0);
                    float pitch = preferences.getFloat("pitch",0);

                    Locale[] locales = Locale.getAvailableLocales();

                    //Log.d("TTS Check : ",language);
                    //textToSpeech.setLanguage(locale);
                    textToSpeech.setSpeechRate(speed);
                    textToSpeech.setPitch(pitch);

                    for(Locale locale : locales){
                        if(locale.getDisplayName().equals(language)){
                            textToSpeech.setLanguage(locale);
                            textToSpeech.setSpeechRate(speed);
                            textToSpeech.setPitch(pitch);
                            break;
                        }
                    }

                }

                if (status == TextToSpeech.SUCCESS) {                               // If success do as follow

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {                    // TextToSpeech finish a queue, start another paragraph/start another TextToSpeech

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if ((!lollipopStopControl || lollipopStartTTS) && trackParagraph < paragraph.size()) {

                                    lollipopStartTTS = false;

                                    if(!lollipopPreviousControl && !lollipopNextControl){
                                        trackParagraph++;
                                        Log.d("Check Flow Normal: ",trackParagraph + " // " + paragraph.size() + " // " + lollipopPreviousControl + " // " + lollipopForwardControl);
                                    }

                                    if(lollipopNextClicked && !lollipopPreviousClicked){

                                        if(lollipopNextControl){
                                            lollipopNextClicked = false;
                                            lollipopNextControl = false;
                                            Log.d("Check Flow Forward: ",trackParagraph + " // " + paragraph.size() + " // " + lollipopPreviousControl + " // " + lollipopForwardControl);
                                        }
                                        else if(trackParagraph < paragraph.size() && !lollipopNextControl)
                                            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");
                                    }
                                    else if(!lollipopNextClicked && lollipopPreviousClicked){
                                        if(lollipopPreviousControl){
                                            Log.d("Check track <-: ","onDone B " + trackParagraph);
                                            //trackParagraph++;
                                            Log.d("Check track <-: ","onDone A " + trackParagraph);
                                            lollipopPreviousControl = false;
                                            lollipopPreviousClicked = false;
                                        }
                                        else if (trackParagraph < paragraph.size() && !lollipopPreviousControl)
                                            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");
                                    }
                                    else
                                        textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");

                                    if(trackParagraph >= paragraph.size() && !lollipopFinishReadCheck){

                                            lollipopFinishReadCheck = true;
                                            textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech


                                    }

                                    Log.d("Check Flow: ",trackParagraph + " // " + paragraph.get(trackParagraph));
                                }

                                else if(lollipopFinishReadCheck && trackParagraph >= paragraph.size()){
                                    Log.d("Check Flow Here: ",trackParagraph + " // " + paragraph.size());

                                    swapPlayIcon(buttonPlay_Pause);
                                    trackParagraph = 0;
                                    textToSpeech.stop();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    if(preferences.getBoolean("autoplay",true)){
                                        olderNews();

                                    }
                                }
                            }

                                else if(lollipopFinishReadCheck && trackParagraph >= paragraph.size()){
                                    Log.d("Check Flow Here: ",trackParagraph + " // " + paragraph.size());

                                    swapPlayIcon(buttonPlay_Pause);
                                    trackParagraph = 0;
                                    textToSpeech.stop();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    if(preferences.getBoolean("autoplay",true)){
                                        olderNews();

                                    }
                                }

                            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                                if(!kitkatStopControl && trackParagraph < paragraph.size()){

                                    if(!kitKatNextControl && !kitkatPreviousControl)
                                        trackParagraph++;

                                    if(kitKatNextControl)
                                        kitKatNextControl = false;

                                    if(kitkatPreviousControl)
                                        kitkatPreviousControl = false;

                                    if(trackParagraph < paragraph.size())
                                        textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                                    Log.d("Check Flow: ",trackParagraph + " // " + paragraph.size());

                                    if(trackParagraph >= paragraph.size() && !kitkatFinishReadCheck){
                                        kitkatFinishReadCheck = true;
                                        textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                                    }

                                }

                                else if(kitkatFinishReadCheck && trackParagraph == paragraph.size()){
                                    Log.d("Check Flow Here: ",trackParagraph + " // " + paragraph.size());

                                    swapPlayIcon(buttonPlay_Pause);
                                    trackParagraph = 0;
                                    textToSpeech.stop();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    if(preferences.getBoolean("autoplay",true)){
                                        olderNews();

                                    }
                                }

                            }
                            }



                        @Override
                        public void onError(String utteranceId) {

                        }

                    });

                }
            }
        });

        //Assign TTS engine for paraText / for onInit
        paramsLollipop.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        paramsKitkat.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        automaticPlay(0);
    }

    //Retrieve file name from the directory where the folder is store
    public void getFileName() {
        String fileSearch;
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/voice-the-news/");
        File tempo = null;

        switch (language){
            case "EN":
                tempo =  new File(root,"lang-en");
                break;
            case "BM":
                tempo = new File(root,"lang-bm");
                break;
            case "CN":
                tempo = new File(root,"lang-cn");
        }

        for (File file : tempo.listFiles()) {
            String line;
            String para;

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                fileSearch = file.getName();
                fileSearch = fileSearch.substring(0, fileSearch.indexOf("."));

                if ((fileContents.get(layoutPosition).getFilename() == Integer.parseInt(fileSearch)) && !paragraph.get(0).isEmpty()) {
                    int count = 0;

                    //Test output area

                    while ((line = br.readLine()) != null){

                        if(count > 3){
                            para = line;
                            this.paragraph.add(para);
                        }

                        count++;

                    }

                }
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            textToSpeech.stop();
            textToSpeech.shutdown();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            //Newer news
            case R.id.zero:
                if(layoutPosition - 1 >= 0) {
                    newerNews();
                }

                else
                    Toast.makeText(getApplicationContext(), "The latest news", Toast.LENGTH_SHORT).show();
                break;

            //Previous sentence
            case R.id.one:
                if(trackParagraph - 1 >= 0 && !paragraph.isEmpty()) {
                    previousSpeech();
                    Toast.makeText(getApplicationContext(), "Previous sentence", Toast.LENGTH_SHORT).show();
                    stopControl = false;
                }
                else
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();

                break;
            //Play & Pause
            case R.id.two:
                if(paragraph == null || paragraph.size() == 0)
                    Toast.makeText(getApplicationContext(), "Getting ready", Toast.LENGTH_SHORT).show();
                else
                    pauseplaySpeech(view);

                break;
            //Stop
            case R.id.three:
                stopControl = true;
                stopSpeech();
                Toast.makeText(getApplicationContext(), "Reading Stop", Toast.LENGTH_SHORT).show();
                swapPlayIcon(buttonPlay_Pause);

                break;
            //Next sentence
            case R.id.four:
                if(trackParagraph + 1 < paragraph.size() && !paragraph.isEmpty()){
                    nextSpeech();
                    Toast.makeText(getApplicationContext(), "Next sentence", Toast.LENGTH_SHORT).show();
                    stopControl = false;
                }
                else
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();

                break;
            //Older news
            case R.id.five:
                if(layoutPosition + 1 < fileContents.size()) {
                    olderNews();
                }
                else
                    Toast.makeText(getApplicationContext(), "The oldest news", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    //Method for Buttonforward
    public void nextSpeech(){
        //Stop reading -> increase tracker -> start reading with new tracker value
        Log.d("Next Speech after: ","" + trackParagraph);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            textToSpeech.stop();
            lollipopNextClicked = true;
            lollipopNextControl = true;

            trackParagraph++;
            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");

        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ){
            textToSpeech.stop();
            kitkatStopControl = false;

            kitKatNextControl = true;
            trackParagraph++;
            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
        }

        //Above function cause to Text-to-Speech to triggered. Thus switch icon to pause icon
        Log.d("Next Speech after: ","" + trackParagraph);
        swapPauseIcon(buttonPlay_Pause);
    }

    //Method for buttonRewind
    public void previousSpeech(){
        //Stop reading -> decrease tracker -> start reading with new tracker value

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.stop();
            lollipopPreviousClicked = true;
            lollipopPreviousControl = true;
            trackParagraph--;
            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            textToSpeech.stop();
            kitkatPreviousControl = true;
            if(trackParagraph - 1 >= 0)
                trackParagraph--;
            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech

        }

        //Above function cause to Text-to-Speech to triggered. Thus switch icon to pause icon
        swapPauseIcon(buttonPlay_Pause);
    }

    //Method for play_pauseButton
    public void pauseplaySpeech(View view){
        stopControl = false;
        ImageView viewPause = (ImageView) view;
        // Check for reading; True - pause & change to play icon / False - play & change to pause icon
        if(textToSpeech.isSpeaking()){
            kitkatStopControl = true;
            lollipopStopControl = true;
            textToSpeech.stop();
            viewPause.setImageResource(R.drawable.icon_play);
            Toast.makeText(getApplicationContext(), "Reading Pause", Toast.LENGTH_SHORT).show();
        }

        else if(!textToSpeech.isSpeaking()){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lollipopStopControl = false;
                textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech
            }

            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                kitkatStopControl = false;
                textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech

            }

            viewPause.setImageResource(R.drawable.icon_pause);
            Toast.makeText(getApplicationContext(), "Reading Start", Toast.LENGTH_SHORT).show();

        }
    }

    public void stopSpeech(){
        kitkatStopControl = true;
        lollipopStopControl = true;
        textToSpeech.stop();
        trackParagraph = 0;
    }

    //Swap icon to play icon
    public void swapPlayIcon(final View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView viewPlay = (ImageView) view;
                viewPlay.setImageResource(R.drawable.icon_play);

            }
        });
    }

    //Swap icon to pause icon
    public void swapPauseIcon(final View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView viewPlay = (ImageView) view;
                viewPlay.setImageResource(R.drawable.icon_pause);

            }
        });
    }

    public void newerNews(){
        Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("link", fileContents);
        bundle.putInt("position", layoutPosition - 1);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        textToSpeech.stop();
        textToSpeech.shutdown();
        finish();
    }

    public void olderNews(){
        Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("link", fileContents);
        bundle.putInt("position", layoutPosition + 1);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        textToSpeech.stop();
        textToSpeech.shutdown();
        finish();
    }

    public void automaticPlay(final int count){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(preferences.getBoolean("autoplay",true)){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    if(!paragraph.isEmpty()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                            kitkatStopControl = false;
                            textToSpeech.speak(paragraph.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                            Toast.makeText(getApplicationContext(), "Reading Start", Toast.LENGTH_SHORT).show();
                        }
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            lollipopStopControl = false;
                            textToSpeech.speak(paragraph.get(trackParagraph),TextToSpeech.QUEUE_FLUSH,paramsLollipop,"paragraph");
                            lollipopStartTTS = true;
                        }

                        swapPauseIcon(buttonPlay_Pause);
                    }

                    else{
                        automaticPlay(count + 1);
                    }

                }
            }, 2500);

        }

    }


}
