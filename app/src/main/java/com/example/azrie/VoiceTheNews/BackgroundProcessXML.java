package com.example.azrie.VoiceTheNews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Azrie on 11/11/2016.
 */

public class BackgroundProcessXML extends AsyncTask<Void, Void,Void> {

    private ActivityMainR activityMain;

    ProgressDialog progressDialog;

    private Context context;
    private String address;
    private ArrayList<DataXML> newsListXML; // Retrieve from XML for card/recyclerview usage

    public BackgroundProcessXML(ActivityMainR activityMain, Context context, String address){

        this.activityMain = activityMain;
        this.context = context;
        this.address = address;

        progressDialog  = new ProgressDialog(this.context);
        progressDialog.setMessage("loading");

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        retrieveXML();
        //retrieveHTML(newsUrl,newsListHTML);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Set newsList in main
        newsListXML = activityMain.setReadList(newsListXML);
        activityMain.setNewsList(newsListXML,"recent");


        if(!newsListXML.isEmpty()){
            setExecuteReadHTML(newsListXML);
        }

        progressDialog.dismiss();
    }

    /**
     * Core Method - Read / Downloading / Storing XML & HTML content
     */

    //Retrieve XML tags from news website and store tags inside ArrayList of DataXML
    private void retrieveXML(){

        newsListXML = new ArrayList<DataXML>();

        try{

            Document document = Jsoup.connect(address).get();

            String title = null;
            String description = null;
            String date = null;
            String link = null;
            boolean read = false;


            for(Element item : document.select("item")){
                Elements children = item.children();

                for(Element child : children){

                    if(child.tagName().equalsIgnoreCase("title")){

                        title = child.getElementsByTag("title").text();
                    }

                    else if (child.tagName().equalsIgnoreCase("description")){

                        description = child.getElementsByTag("description").text();
                        description = cleanHTML(description);
                    }

                    else if (child.tagName().equalsIgnoreCase("pubDate")){

                        date = child.getElementsByTag("pubDate").text();
                        date = getDateInMillis(date).toString();
                    }

                    else if (child.tagName().equalsIgnoreCase("link")){

                        link = child.getElementsByTag("link").text();
                    }
                }
                newsListXML.add(new DataXML(title,date,link,description,read));
                //Test output space
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setExecuteReadHTML(ArrayList<DataXML> list)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(preferences.getBoolean("autoDownloadPreference",true)){

            for(int i = 0; i < list.size(); i++) {
                new BackgroundProcessHTML(context, list.get(i).getLink()).execute();
            }
        }

        else {

            Toast.makeText(context,"Access folder permission required", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Supporting Method
     */


    //Remove HTML tag from String
    private String cleanHTML(String string){

        string = string.replaceAll("\\<.*?>","");
        string = string.replace("&amp;", "&").replace("&quot;", "\"").replace("&rsquo;","’").replace("&#39;","'").replace("&nbsp;"," ");

        return string;
    }

    //Convert full time format to hours ago format (current time - post time)
    private static CharSequence getDateInMillis(String srcDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());

        long dateInMillis = 0;
        try {
            Date date = formatter.parse(srcDate);
            dateInMillis = date.getTime();
            CharSequence time = DateUtils.getRelativeTimeSpanString(dateInMillis,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_NUMERIC_DATE);

            return time;
        }

        catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return srcDate;
    }

}
