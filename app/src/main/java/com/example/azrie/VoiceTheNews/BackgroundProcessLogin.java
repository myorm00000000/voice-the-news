package com.example.azrie.VoiceTheNews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by moham on 08/12/2016.
 */

public class BackgroundProcessLogin extends AsyncTask<Void,Void,Void> {

    private LoginActivity loginActivity;
    private Context context;
    private String username,password;
    private WebView webView;

    public BackgroundProcessLogin(LoginActivity loginActivity, Context context, String username, String password){

        this.loginActivity = loginActivity;
        this.context = context;
        this.username = username;
        this.password = password;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        //webviewLogin(username,password);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        webviewLogin(username,password);

    }

    private void login(String username, String password){

        try {

            String userAgent = System.getProperty( "http.agent" );

            /*Connection.Response response = Jsoup.connect("http://login2.mkini.net/v2/login-exchange")
                    .userAgent(userAgent)
                    .data("platform","desktop",
                            "next_url","http://www.malaysiakini.com/en",
                            "version","1",
                            "keeplogin","1",
                            "language","en",
                            "callback_url","http://www.malaysiakini.com/login/en/callback",
                            "username", "ianchai",
                            "password", "sinsoi",
                            "lkeeplogin","on")
                    .method(Connection.Method.POST)
                    .execute();*/

            Connection.Response response = Jsoup.connect("http://www.malaysiakini.com/")
                    .userAgent(userAgent)
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = Jsoup.connect("http://login2.mkini.net/v2/login-exchange")
                .userAgent(userAgent)
                .cookies(response.cookies())
                .data("platform","desktop")
                .data("next_url","http://www.malaysiakini.com/en")
                .data("version","1")
                .data("keeplogin","1")
                .data("language","en")
                .data("callback_url","http://www.malaysiakini.com/login/en/callback")
                .data("username", "ianchai")
                .data("password", "sinsoi")
                .data("lkeeplogin","on")
                .post();

            String cookie = response.cookie("__cfduid");

            Thread.sleep(0);

            Document document = Jsoup.connect("http://www.malaysiakini.com/en")
                    .cookie("__cfduid",cookie)
                    .get();

            //Log.d("Login Data "," Document : " + document + " // User agent " + userAgent + " // Cooksie " + cookie);
            Log.d("Login Data "," Document : " + document + " // Cooksie " + cookie);

            Elements loginData = document.select("logindata");

            Elements hiddenData = doc.select("input[type=hidden]");

            /*ArrayList newsContent  = new ArrayList<String>();

            Elements title = document.select("meta[property=og:title]");
            newsContent.add(title.attr("content"));

            Elements select = document.select("div[id=article_content] > p");
            Element tempParagraph;
            for (int i = 0; i < select.size(); i++){
                tempParagraph = select.get(i);
                newsContent.add(tempParagraph.text());
                Log.d("Login Data "," Test : " + tempParagraph.text());

            }*/

            if(!loginData.isEmpty()){
                Log.d("Login Data "," Fail : " + hiddenData);
            }

            else
                Log.d("Login Data "," Success : " + loginData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void webviewLogin(final String username, final String password){

        webView = new WebView(context);
        webView.loadUrl("http://m.malaysiakini.com/login/en/form");

        webView.getSettings().setJavaScriptEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            int checkLoad = 0;
            String user = "ianchai";
            String pass = "sinsoi";
            ArrayList<String> newsContent  = new ArrayList<String>();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                /*view.loadUrl("javascript: {" +
                        "$f = $('form:first');" +
                        "$('form:first #inputUser').val('" + user + "');"+
                        "$('form:first #inputPassword').val('" + pass + "');"+
                        "$f.submit();}");


                        "$f = $('form:first');" +
                        "$('form:first #inputUser').val('" + user + "');"+
                        "$('form:first #inputPassword').val('" + pass + "');"+
                        "$f.submit();*/

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.d("Check URL ",url + " // Count : " + checkLoad);

                if(checkLoad < 1){
                    view.loadUrl("javascript: {" +
                            "$('input#username').val('" + username + "' );\n" +
                            "$('input#password').val('" + password + "');\n" +
                            "document.querySelectorAll(\"button[type='submit']\")[0].click();}");
                    checkLoad++;

                    Log.d("Check URL in IF",url);

                    //cookies = CookieManager.getInstance().getCookie(url);
                    //Log.d("Cookie Test : ", "All the cookies in a string:" + cookies);

                    if (Build.VERSION.SDK_INT >= 21) {
                        // AppRTC requires third party cookies to work
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptThirdPartyCookies(webView, true);
                    }

                }

                loginActivity.verifyResult(true,url);

            }
        });


    }
}
