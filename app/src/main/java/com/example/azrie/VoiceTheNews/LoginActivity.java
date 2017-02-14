package com.example.azrie.VoiceTheNews;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by moham on 08/12/2016.
 */

public class LoginActivity extends Activity {

    private Button loginButton;
    private Button contButton;
    private Button languageButton;
    private Button englishButton;
    private Button bahasaButton;
    private Button chineseButton;
    private TextView mLink;
    private EditText username;
    private EditText password;

    private String address = null;

    private boolean check = false;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences.Editor saveEditor = languagePreference.edit();
        //saveEditor.putString("language","EN");
        //saveEditor.apply();

        String language = preferences.getString("language",null);
        Boolean login = preferences.getBoolean("login",true);

        Log.d("Check login", String.valueOf(login));

        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCanceledOnTouchOutside(true);

        BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    // DO WHATEVER YOU WANT.
                    finish();
                }
            }
        };

        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));

        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        contButton = (Button) findViewById(R.id.cont_button);
        languageButton = (Button) findViewById(R.id.button_language);

        if (language != null && !language.isEmpty())
            languageButton.setText(language);

        if(preferences.getBoolean("login",true))
            startLogin();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        mLink = (TextView) findViewById(R.id.not_user);
        if (mLink != null) {
            mLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLanguage();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {

                    if(TextUtils.isEmpty(username.getText().toString())){
                        username.setError("Username field cannot be empty");
                    }
                    else if(TextUtils.isEmpty(password.getText().toString())){
                        password.setError("Password field cannot be empty");
                    }
                }

                else{
                    progressDialog.show();
                    new BackgroundProcessLogin(LoginActivity.this, getApplicationContext(), username.getText().toString(), password.getText().toString()).execute();
                }
            }
        });

        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(getApplicationContext(),ActivityMainR.class);
                overridePendingTransition(0,0);

                if(address != null){

                    Bundle bundle = new Bundle();
                    bundle.putString("newsUrl",address);
                    myIntent.putExtra("bundleAddress",bundle);
                    finish();
                    startActivity(myIntent);
                }
                else{
                    finish();
                    startActivity(myIntent);
                }

            }
        });


    }

    public void verifyResult(final boolean check, String url){

        if(url.contains("OK")){
            this.progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    SharedPreferences loginPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor saveEditor = loginPreference.edit();
                    saveEditor.putBoolean("login",true);
                    saveEditor.apply();

                    Intent myIntent = new Intent(getApplicationContext(),ActivityMainR.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("checkLogin",check);
                    myIntent.putExtra("loginBundle",bundle);
                    startActivity(myIntent);
                    overridePendingTransition(0,0);
                }
            }, 1000);
        }

        else if(url.contains("FAIL")){
            this.progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login Fail", Toast.LENGTH_LONG).show();
        }
    }

    private void getLanguage(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_language_option);
        dialog.show();
        Window window = dialog.getWindow();

        englishButton = (Button) dialog.findViewById(R.id.button_english);
        bahasaButton = (Button) dialog.findViewById(R.id.button_bahasa);
        chineseButton = (Button) dialog.findViewById(R.id.button_chinese);

        //Percentage calculation for screen percentage
        Point size = new Point();
        Display display = null;
        if (window != null) {
            display = window.getWindowManager().getDefaultDisplay();
        }
        display.getSize(size);
        int height = size.y;
        int width = size.x;

        window.setLayout((int) (width * 0.93),(int) (height * 0.40));

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageButton.setText("EN");
                languagePreference("EN");
                address = "http://www.malaysiakini.com/en/news.rss";
                dialog.dismiss();

            }
        });


        bahasaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageButton.setText("BM");
                languagePreference("BM");
                address = "http://www.malaysiakini.com/my/news.rss";
                check = true;
                callMainBM();
                dialog.dismiss();

            }
        });


        chineseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageButton.setText("CN");
                languagePreference("CN");
                address = "http://www.malaysiakini.com/cn/news.rss";
                dialog.dismiss();

            }
        });

    }

    private void callMainBM(){
        if(check){

            Toast.makeText(getApplicationContext(),"Starting News in BM", Toast.LENGTH_SHORT).show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 2s
                    Intent myIntent = new Intent(getApplicationContext(),ActivityMainR.class);
                    overridePendingTransition(0,0);
                    Bundle bundle = new Bundle();
                    bundle.putString("newsUrl",address);
                    myIntent.putExtra("bundleAddress",bundle);
                    startActivity(myIntent);
                }
            }, 2000);
        }
    }

    private void languagePreference(String language){
        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor saveEditor = languagePreference.edit();
        saveEditor.putString("language",language);
        saveEditor.apply();
    }

    private void startLogin(){

        Toast.makeText(getApplicationContext(),"Logged in", Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s
            Intent myIntent = new Intent(getApplicationContext(),ActivityMainR.class);
            overridePendingTransition(0,0);
            finish();
            startActivity(myIntent);
            }
        }, 2000);

    }

}
