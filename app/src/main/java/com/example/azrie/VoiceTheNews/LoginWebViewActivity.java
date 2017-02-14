package com.example.azrie.VoiceTheNews;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
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
 * Created by moham on 12/12/2016.
 */

public class LoginWebViewActivity extends Activity {

    private WebView webView;
    private String cookies;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getBundleExtra("bundle");

        if(extras != null) {
            username = extras.getString("username");
            password = extras.getString("password");
        }

        setContentView(R.layout.activity_login_webview);

        webView =(WebView)findViewById(R.id.login_web_view);

        webView.loadUrl("http://m.malaysiakini.com/login/en/form");

        webView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            int checkLoad = 0;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.d("Check URL ",url);

                if(checkLoad < 1){
                    view.loadUrl("javascript: {" +
                            "$('input#username').val('" + username + "' );\n" +
                            "$('input#password').val('" + password + "');\n" +
                            "document.querySelectorAll(\"button[type='submit']\")[0].click();}");
                            checkLoad++;

                    cookies = CookieManager.getInstance().getCookie(url);

                    if (Build.VERSION.SDK_INT >= 21) {
                        // AppRTC requires third party cookies to work
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptThirdPartyCookies(webView, true);
                    }
                 }
            }
        });

    }


}
