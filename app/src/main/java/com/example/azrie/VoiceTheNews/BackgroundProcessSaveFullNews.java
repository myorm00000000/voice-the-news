package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by moham on 26/1/2017.
 */

public class BackgroundProcessSaveFullNews extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String newsUrl;
    private String language;
    private ArrayList<String> newsDetails = new ArrayList<String>();
    private ArrayList<String> newsParagraph;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(context);
        language = languagePreference.getString("language","DEFAULT");

        Log.d("Preference Language:",language);

    }

    BackgroundProcessSaveFullNews(Context context, String newsUrl, ArrayList<String> contentNews){

        this.newsUrl = newsUrl;
        this.context = context;
        this.newsParagraph = contentNews;
    }

    @Override
    protected Void doInBackground(Void... params) {

        storeFullNews(newsUrl, newsParagraph);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void storeFullNews(String newsUrl, ArrayList<String> newsParagraph){

        try {

            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/voice-the-news/");
            File rootEn = new File(root,"lang-en");
            File rootBm = new File(root,"lang-bm");
            File rootCn = new File(root,"lang-cn");

            File tempo = null;

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

            newsUrl = newsUrl.substring(newsUrl.lastIndexOf("/") + 1);

            File newsFile = new File(tempo, newsUrl + ".txt");

            if(newsFile.exists()) {
                int count = 0;
                String para;
                String line;
                BufferedReader br = new BufferedReader(new FileReader(newsFile));

                while ((line = br.readLine()) != null){
                    //Log.d("Check Line Exist"," :" + line);
                    if(count < 4){
                        para = line;
                        newsDetails.add(para);
                    }

                    count++;
                }

                br.close();
                newsFile.delete();
            }

            if(!newsFile.exists()) {
                //Log.d("Check File Exist","File !Exist : " + newsUrl);

                FileWriter writer = new FileWriter(newsFile);

                for(int i = 0; i < newsDetails.size(); i++) {
                    writer.append(newsDetails.get(i) + System.lineSeparator());
                }

                for(int i = 0; i < newsParagraph.size(); i++) {
                    writer.append(newsParagraph.get(i) + System.lineSeparator());;
                }

                writer.flush();
                writer.close();
                //Log.d("Check File Exist",newsUrl + " created");
            }

            //Test output space
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
