package com.example.azrie.VoiceTheNews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**sadasd
 * Created by Azrie on 27/7/2016.
 */
public class ActivityNewsWeb extends Activity implements View.OnClickListener/**, AsyncResponse*/ {
    private View buttonPrevious, buttonRewind, buttonPlay_Pause, buttonStop, buttonForward, buttonNext;  //Initialize buttons inside toolbar

    private WebView webview;             // Initialize WebView
    private TextToSpeech textToSpeech;   // Initialize TextToSpeech

    private ArrayList<String> paragraphHTML = new ArrayList<String>();
    private ArrayList<DataXML> linkURL;

    private Bundle paramsLollipop = new Bundle();//For InitListener
    private HashMap<String, String> paramsKitkat = new HashMap<String, String>();

    private boolean stopControl = false;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean kitKatNextControl = false;
    private boolean kitkatStopControl = false;
    private boolean kitkatPreviousControl = false;
    private boolean kitkatFinishReadCheck = false;
    private boolean kitKatAutoControl = false;
    private boolean lollipopPreviousControl = false;
    private boolean lollipopForwardControl = false;
    private boolean lollipopStopControl = false;
    private boolean lollipopStartTTS = false;
    private boolean lollipopNextControl = false;
    private boolean lollipopFinishReadCheck = false;
    private boolean checkDownload = false;
    private boolean fullNewsCheck = false;
    private boolean newsCheck = false;
    private boolean lollipopPreviousClicked = false;
    private boolean lollipopNextClicked = false;


    private int trackParagraph = 0;      // To keep track of current paragraph location
    private int layoutPosition;
    private int originalSize = 0;

    private String newsUrl = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();

        new BackgroundProcessTTS(ActivityNewsWeb.this, getApplicationContext(), newsUrl).execute();
        if(!SingletonReadList.getInstance().getReadList().contains(linkURL.get(layoutPosition).getId())) {
            SingletonReadList.getInstance().getReadList().add(linkURL.get(layoutPosition).getId());
        }
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

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

        progressDialog  = new ProgressDialog(this);

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
                        public void onDone(String utteranceId) {                    // TextToSpeech finish a queue, start another paragraph/start another TextToSpeech

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                if ((!lollipopStopControl || lollipopStartTTS) && trackParagraph < paragraphHTML.size()) {

                                    lollipopStartTTS = false;

                                    if(!lollipopPreviousControl && !lollipopNextControl){
                                        trackParagraph++;
                                        Log.d("Check Flow Normal: ",trackParagraph + " // " + paragraphHTML.size() + " // " + lollipopPreviousControl + " // " + lollipopForwardControl);
                                    }

                                    if(lollipopNextClicked && !lollipopPreviousClicked){

                                        if(lollipopNextControl){
                                            lollipopNextClicked = false;
                                            lollipopNextControl = false;
                                            Log.d("Check Flow Forward: ",trackParagraph + " // " + paragraphHTML.size() + " // " + lollipopPreviousControl + " // " + lollipopForwardControl);
                                        }
                                        else if(trackParagraph < paragraphHTML.size() && !lollipopNextControl)
                                            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");
                                    }
                                    else if(!lollipopNextClicked && lollipopPreviousClicked){
                                        if(lollipopPreviousControl){
                                            Log.d("Check track <-: ","onDone B " + trackParagraph);
                                            //trackParagraph++;
                                            Log.d("Check track <-: ","onDone A " + trackParagraph);
                                            lollipopPreviousControl = false;
                                            lollipopPreviousClicked = false;
                                        }
                                        else if (trackParagraph < paragraphHTML.size() && !lollipopPreviousControl)
                                            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");
                                    }
                                    else
                                        textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");

                                    if(trackParagraph >= paragraphHTML.size() && !lollipopFinishReadCheck){
                                        if(fullNewsCheck){
                                            lollipopFinishReadCheck = true;
                                            textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech
                                            Log.d("Check Boolean: ",lollipopFinishReadCheck + " // " + fullNewsCheck + " // " + trackParagraph);
                                        }
                                    }

                                    Log.d("Check Flow: ",trackParagraph + " // " + paragraphHTML.get(trackParagraph));
                                }

                                else if(lollipopFinishReadCheck && fullNewsCheck && trackParagraph >= paragraphHTML.size()){
                                    Log.d("Check Flow Here: ",trackParagraph + " // " + paragraphHTML.size());

                                    swapPlayIcon(buttonPlay_Pause);
                                    trackParagraph = 0;
                                    textToSpeech.stop();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    if(preferences.getBoolean("autoplay",true)){
                                        olderNews();

                                    }
                                }
                            }

                            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){

                                if(!kitkatStopControl && trackParagraph < paragraphHTML.size()){

                                    if(!kitKatNextControl && !kitkatPreviousControl)
                                        trackParagraph++;

                                    if(kitKatNextControl)
                                        kitKatNextControl = false;

                                    if(kitkatPreviousControl){
                                        //trackParagraph++;
                                        kitkatPreviousControl = false;
                                    }

                                    if(trackParagraph < paragraphHTML.size())
                                        textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                                    Log.d("Check Flow: ",trackParagraph + " // " + paragraphHTML.size());

                                    if(trackParagraph >= paragraphHTML.size() && !kitkatFinishReadCheck){
                                        kitkatFinishReadCheck = true;
                                        textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                                    }

                                }

                                else if(kitkatFinishReadCheck && fullNewsCheck && trackParagraph == paragraphHTML.size()){
                                    Log.d("Check Flow Here: ",trackParagraph + " // " + paragraphHTML.size());

                                    swapPlayIcon(buttonPlay_Pause);
                                    trackParagraph = 0;
                                    textToSpeech.stop();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    if(preferences.getBoolean("autoplay",true)){
                                        olderNews();

                                        /*new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        }, 6000);*/
                                    }
                                }

                            }

                            //Log.d("TTS Check Flow: ", trackParagraph +  " // " + paragraphHTML.size() + " // " + lollipopNextControl);
                            //if(trackParagraph < paragraphHTML.size() /*&& !lollipopNextControl*/){      // Check where location of the TTS at now
                                                                                                    //Increment TTS tracker and play supposedly next paragraph

                                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                    if ((!lollipopStopControl) || lollipopStartTTS) {

                                        lollipopStartTTS = false;

                                        if(!lollipopPreviousControl && !lollipopForwardControl){
                                            trackParagraph++;
                                        }

                                        if(lollipopForwardControl){
                                            lollipopForwardControl = false;
                                        }

                                        if(lollipopPreviousControl){
                                            lollipopPreviousControl = false;
                                        }
                                        else
                                            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_ADD, paramsLollipop, "paragraph");
                                    }

                                }


                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !kitkatStopControl){
                                    if(!kitkatPreviousControl)
                                        trackParagraph++;
                                    else
                                        kitkatPreviousControl = false;

                                    textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                                }

                                if(trackParagraph == paragraphHTML.size()-1){
                                    lollipopNextControl = true;
                                }
                            }*/

                            /*if(!stopControl && fullNewsCheck){

                                swapPlayIcon(buttonPlay_Pause);
                                trackParagraph = 0;
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                if(preferences.getBoolean("autoplay",true)){
                                    olderNews();
                                }
                            }*/

                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                        }
                    });

                }
            }
        });

        // Retrieve URL link from Adapter
        Bundle extras = getIntent().getBundleExtra("bundle");

        if(extras != null) {
            linkURL = extras.getParcelableArrayList("link");
            layoutPosition = extras.getInt("position");
            newsUrl = linkURL.get(layoutPosition).getLink();
        }

        // Fire webView with retrieve URL
        webViewLoadURL();

        //Assign TTS engine for paraText / for onInit
        paramsLollipop.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "paragraph");
        paramsKitkat.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        automaticPlay(0);

    }


    // WebView settings and paragraph extraction
    public void webViewLoadURL(){

        webview = (WebView) findViewById(R.id.webView);

        webview.clearCache(true);
        webview.clearHistory();

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.getSettings().setBlockNetworkImage(false);
        webview.addJavascriptInterface(this, "myJava");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if(preferences.getBoolean("login",true)){
                    progressDialog.setMessage("Verifying...");
                    progressDialog.setCanceledOnTouchOutside(false);
                }


            }


            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                //This function will be registered as a JavaScript interface
                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("Lollipop JS: ","Inside onPageFinished");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            view.loadUrl("javascript: {" +
                                    "var list = [];" +
                                    "var title = document.getElementsByTagName(\"h1\")[0].innerText;" +
                                    "list[0] = title;" +
                                    "var value = document.getElementsByClassName(\"mk-content-text uk-margin-bottom local-content-body\")[0].getElementsByTagName(\"p\");" +
                                    "for(var i = 0; i < value.length; i++){" +
                                    "list[i+1] = value[i].innerText;" +
                                    "}" +
                                    "window.myJava.onData(list,'test');" +
                                    "}");
                        }
                        else {
                            view.evaluateJavascript("javascript:" +
                                    "var list = [];" +
                                    "var title = document.getElementsByTagName(\"h1\")[0].innerText;" +
                                    "list[0] = title;" +
                                    "var value = document.getElementsByClassName(\"mk-content-text uk-margin-bottom local-content-body\")[0].getElementsByTagName(\"p\");" +
                                    "for(var i = 0; i < value.length; i++){" +
                                    "list[i+1] = value[i].innerText;" +
                                    "}" +
                                    "window.myJava.onData(list,'test');" ,null);
                        }
                    }
                }, 5000);

            }
        });

        webview.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= 19) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webview.loadUrl(newsUrl);

    }

    //This method will be registered as a JavaScript interface
    @JavascriptInterface
    @SuppressWarnings("unused")
    //public void onData(final String[] value){
    public void onData(final String[] value, String test){

        final ArrayList<String> newsContent  = new ArrayList<String>();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(newsContent.isEmpty() && !newsCheck){
                    if(value.length > 0){
                        for(int i = 0; i < value.length; i++){
                            newsContent.add(value[i]);
                            Log.d("newsContent Check : ",newsContent.get(i));
                        }
                    }

                    Log.d("size check : ",paragraphHTML.size() + " // " + newsContent.size());

                    if(originalSize == newsContent.size()){

                        fullNewsCheck = true;

                        if(trackParagraph >= paragraphHTML.size() - 1){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech
                                Log.d("Load check : "," Here");
                            }
                            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                                textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                            }
                        }

                    }

                    if(originalSize != newsContent.size()){

                        Log.d("Full News Check : ","Full News // " + trackParagraph);

                        fullNewsCheck = true;

                        for(int i = paragraphHTML.size(); i < newsContent.size(); i++){
                            paragraphHTML.add(newsContent.get(i));
                            Log.d("paragraphHTML Check : ",paragraphHTML.get(i));
                        }

                        if(trackParagraph < originalSize){
                            Log.d("Full News Check : "," Track < //" + trackParagraph);
                            trackParagraph = originalSize - 2;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Log.d("Full News Check : "," Track < IF //" + trackParagraph);
                                textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech

                            }
                            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                                textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech

                            }

                        }

                        if(trackParagraph >= originalSize - 1){

                            Log.d("Full News Check : "," Track > //" + trackParagraph);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Log.d("Full News Check : "," Track > IF //" + trackParagraph);
                                trackParagraph = originalSize;
                                textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech

                            }
                            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                                textToSpeech.speak("Read Finished", TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech

                            }
                        }

                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    if(preferences.getBoolean("autoDownloadPreference",true)){
                        new BackgroundProcessSaveFullNews(getApplicationContext(),newsUrl,newsContent).execute();
                    }

                    newsCheck = true;

                }
            }
        }, 2000);
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
                if(trackParagraph - 1 >= 0 && !paragraphHTML.isEmpty()) {
                    previousSpeech();
                    Toast.makeText(getApplicationContext(), "Previous sentence", Toast.LENGTH_SHORT).show();
                    stopControl = false;
                }
                else
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();

                break;
            //Play & Pause
            case R.id.two:
                if(paragraphHTML == null || paragraphHTML.size() == 0)
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
                if(trackParagraph + 1 < paragraphHTML.size() && !paragraphHTML.isEmpty()){
                    nextSpeech();
                    Toast.makeText(getApplicationContext(), "Next sentence", Toast.LENGTH_SHORT).show();
                    stopControl = false;
                }
                else
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();

                break;
            //Older news
            case R.id.five:
                if(layoutPosition + 1 < linkURL.size()) {
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
            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");

        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ){
            textToSpeech.stop();
            kitkatStopControl = false;

            kitKatNextControl = true;
            trackParagraph++;
            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
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
            Log.d("Check track <-: ","B " + trackParagraph);
            trackParagraph--;
            Log.d("Check track <-: ","A " + trackParagraph);
            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");

        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            textToSpeech.stop();
            kitkatPreviousControl = true;
            if(trackParagraph - 1 >= 0)
                trackParagraph--;
            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech

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
                textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsLollipop, "paragraph");    //Start Text To Speech
            }

            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                kitkatStopControl = false;
                textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech

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


    public void automaticPlay(final int count){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(preferences.getBoolean("autoplay",true)){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    if(checkDownload && !paragraphHTML.isEmpty()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                            kitkatStopControl = false;
                            textToSpeech.speak(paragraphHTML.get(trackParagraph), TextToSpeech.QUEUE_FLUSH, paramsKitkat);    //Start Text To Speech
                            Toast.makeText(getApplicationContext(), "Reading Start", Toast.LENGTH_SHORT).show();
                        }
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            lollipopStopControl = false;
                            textToSpeech.speak(paragraphHTML.get(trackParagraph),TextToSpeech.QUEUE_FLUSH,paramsLollipop,"paragraph");
                            lollipopStartTTS = true;
                        }

                        swapPauseIcon(buttonPlay_Pause);
                    }

                    else{
                        automaticPlay(count + 1);
                    }

                }
            }, 1000);

        }

    }

    public void getNewsContent(ArrayList<String> list){
        this.paragraphHTML = list;
        this.checkDownload = true;
        originalSize = list.size();

    }

    public void newerNews(){
        Intent intent = new Intent(getApplicationContext(), ActivityNewsWeb.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("link", linkURL);
        bundle.putInt("position", layoutPosition - 1);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        textToSpeech.stop();
        textToSpeech.shutdown();
        finish();
    }

    public void olderNews(){
        Intent intent = new Intent(getApplicationContext(), ActivityNewsWeb.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("link", linkURL);
        bundle.putInt("position", layoutPosition + 1);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        textToSpeech.stop();
        textToSpeech.shutdown();
        finish();
    }
}



