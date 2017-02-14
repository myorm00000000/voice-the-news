package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azrie on 13/11/2016.
 */

public class BackgroundProcessTTS extends AsyncTask <Void,Void,Void> {

    private Boolean checkNewsContent = false;
    private String address;

    private ActivityNewsWeb webview;

    private ArrayList<String> newsContent;

    private long startTime;
    private long endTime;

    Context context;

    int checkCount = 0;

    public BackgroundProcessTTS(ActivityNewsWeb webview, Context context, String address){

        this.context = context;
        this.webview = webview;
        this.address = address;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        long doInTime = System.currentTimeMillis();
        double doInTimeConvert = doInTime / 1000.0;
        Log.d("Print Time"," DoIn : " + doInTimeConvert);

        retrieveContent(address);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //retrieveContentWebView(newsUrl);

        long doInTime = System.currentTimeMillis();
        double doInTimeConvert = doInTime / 1000.0;
        Log.d("Print Time"," onPost : " + doInTimeConvert);

        if(newsContent.isEmpty()){
            Log.d("Auto Play Download TT: "," it's empty");

        }

        else{
            Log.d("Auto Play Download TT: "," it's not empty");
            //webview.getNewsContent(newsContent);
        }

        checkNewsContent(0);

        checkCount++;
        //Log.d("TTS CheckCountCheck: ","" + checkCount);
    }

    private void retrieveContent(String address){

        newsContent  = new ArrayList<String>();

        try{
            Document document = Jsoup.connect(address).timeout(0).get();

            Elements title = document.select("meta[property=og:title]");
            newsContent.add(title.attr("content"));

            Elements select = document.select("div[id=article_content] > p");
            Element tempParagraph;
            for (int i = 0; i < select.size(); i++){
                tempParagraph = select.get(i);
                newsContent.add(tempParagraph.text());
                Log.d("Background Check", newsContent.get(i));

            }

            endTime = System.currentTimeMillis();

            long tDelta = endTime - startTime;
            double elapsedSeconds = tDelta / 1000.0;
            double retrieveContent = endTime / 1000.0;

            Log.d("Print Time","" + elapsedSeconds);
            Log.d("Print Time"," retrieveFinish " + retrieveContent);

            /*for(Element paragraph : document.select("div[id=article_content] > p")){
                paragraphHTML.add(paragraph.text());

                Log.d("Check:",""+paragraphHTML.get(checkWebViewRound));
                checkWebViewRound++;
            }*/
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkNewsContent(final int count){

        if(!newsContent.isEmpty()){
            webview.getNewsContent(newsContent);
        }

        else{
            checkNewsContent(count + 1);
            Log.d("Print count","" + count);
        }

    }

    private void retrieveContentWebView(String address){

        newsContent  = new ArrayList<String>();

        final WebView webview;

        webview = new WebView(context);
        webview.loadUrl(address);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "myJava");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.getSettings().setDomStorageEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                view.evaluateJavascript("javascript: {" +
                        "var list = [];" +
                        "var title = document.getElementsByTagName(\"h1\")[0].innerText;" +
                        "list[0] = title;" +
                        "var value = document.getElementsByClassName(\"mk-content-text uk-margin-bottom local-content-body\")[0].getElementsByTagName(\"p\");" +
                        "for(var i = 0; i < value.length; i++){" +
                        "list[i+1] = value[i].innerText;" +
                        "}" +
                        "window.myJava.onData(list);" +
                        "}",null);
            }
        });
    }
}

class MyJavaScriptInterface {

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void onData(final String[] value){

        final ArrayList<String> newsContent  = new ArrayList<String>();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.d("Lollipop TTS JS: ","Inside Javascript / Outside loop");

                if(newsContent.isEmpty()){

                    if(value.length > 0){
                        for(int i = 0; i < value.length; i++){
                            Log.d("Lollipop JS: ","Inside Javascript" + " " + value[i]);
                            newsContent.add(value[i]);
                        }
                    }
                    else
                        //Log.d("Lollipop TTS JS:: ","Inside Javascript" + " " + value[0]);

                    for(int i = 0; i < newsContent.size(); i++){
                        //Log.d("Lollipop TTS JS", newsContent.get(i));
                    }

                }
            }
        }, 1000);


    }
}
