package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azrie on 24/8/2016.
 */
//called in Process_Read_XML
public class BackgroundProcessHTML extends AsyncTask <Void, Void, Void> {


    private String address;
    private String language;
    private Context context;
    private ArrayList<String> retrieveHTML = new ArrayList<String>();

    BackgroundProcessHTML(Context context, String address){

        this.context = context;
        this.address = address;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(context);
        language = languagePreference.getString("language","DEFAULT");

        Log.d("Preference Language:",language);

    }

    @Override
    protected Void doInBackground(Void... voids) {

            retrieveHTML(address, retrieveHTML);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        storeHTML(retrieveHTML,address);

    }

    private void retrieveHTML(String address, ArrayList<String> storeHTML){
        try{

            Document document = Jsoup.connect(address).get();

            Elements title = document.select("meta[property=og:title]");
            storeHTML.add(title.attr("content"));

            Elements description = document.select("meta[property=og:description]");
            storeHTML.add(description.attr("content"));

            Elements url = document.select("meta[property=og:url]");
            storeHTML.add(url.attr("content"));

            Elements published_time = document.select("meta[property=article:published_time]");

            storeHTML.add(published_time.attr("content"));

            Elements divParent = document.select("div[id=article_content]");

            for(Element paragraph : document.select("div[id=article_content] > p")){
                storeHTML.add(paragraph.text());
            }

            this.retrieveHTML = storeHTML;

        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeHTML (ArrayList<String> storeHTML, String address) {

        try {

            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/voice-the-news/");
            File rootEn = new File(root,"lang-en");
            File rootBm = new File(root,"lang-bm");
            File rootCn = new File(root,"lang-cn");

            File tempo = null;

            if (!root.exists() || !rootEn.exists() || !rootBm.exists() || !rootCn.exists()) {

                root.mkdirs();
                rootEn.mkdirs();
                rootBm.mkdirs();
                rootCn.mkdirs();
            }

            switch (language){
                case "EN":
                    tempo = rootEn;
                    break;
                case "BM":
                    tempo = rootBm;
                    break;
                case "CN":
                    tempo = rootCn;
            }

            address = address.substring(address.lastIndexOf("/") + 1);

            File createFile = new File(tempo, address + ".txt");

            if(createFile.exists()) {
             //Log.d("Check File Exist","File Exist : " + address);
            }

            else {
                Log.d("Check File Exist","File !Exist : " + address);

                FileWriter writer = new FileWriter(createFile);

                for(int i = 0; i < storeHTML.size(); i++) {

                    writer.append(storeHTML.get(i) + System.lineSeparator());
                }

                writer.flush();
                writer.close();
                Log.d("Check File Exist",address + " created");
            }

            //Test output space
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

