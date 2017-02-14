package com.example.azrie.VoiceTheNews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Azrie on 01/9/2016.
 */
public class ReadTextFile  extends AsyncTask<Void, Void, Void> {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<DataHTML> fileContents;
    private String language;
    private ProgressDialog progressDialog;


    public ReadTextFile(Context context, RecyclerView recyclerView) {
        //Create a new progress dialog
        this.recyclerView = recyclerView;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");

    }


    @Override
    protected void onPreExecute() {
        //Display progress dialog
        progressDialog.show();
        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(context);
        language = languagePreference.getString("language","DEFAULT");
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        retrieveText();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();

        if (fileContents != null) {
            RecyclerViewOfflineAdapter customAdapter = new RecyclerViewOfflineAdapter(context, fileContents, recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(customAdapter);
        }

        if(fileContents == null)
            Log.d("Check File","null");
        else
            Log.d("Check File","!null");

    }

    public void retrieveText(){

        fileContents = new ArrayList<DataHTML>();
        ArrayList<String> paragraph = new ArrayList<String>();

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/voice-the-news/");
        File tempo = null;
        File filelist[];

        switch (language){
            case "EN":
                tempo =  new File(root,"lang-en");
                break;
            case "BM":
                tempo = new File(root,"lang-bm");
                break;
            case "CN":
                tempo = new File(root,"lang-cn");
                break;
        }


        filelist = tempo.listFiles();

        String filename = null;
        String title = null;
        String description = null;
        String link = null;
        String time = null;
        String unfortmatedTime;
        String para;
        StringBuilder text = new StringBuilder();
        for(File file : filelist){
            String line;
            Log.d("News File name", file.getName());
            try {

                BufferedReader br = new BufferedReader(new FileReader(file));

                if ((line = br.readLine()) != null) {
                    title = line;
                    Log.d("News title is : ", title);


                }

                if ((line = br.readLine()) != null) {
                    description = line;
                    Log.d("News description is : ", description);

                }

                if ((line = br.readLine()) != null) {
                    link = line;
                    Log.d("News link is : ", link);

                }

                if ((line = br.readLine()) != null) {
                    unfortmatedTime = line;
                    Log.d("News unfmtedTime is : ", unfortmatedTime);
                    //check for null and length
                    if(unfortmatedTime != null && unfortmatedTime.length()!=0){
                        unfortmatedTime = unfortmatedTime.substring(0,unfortmatedTime.indexOf("+"));
                        unfortmatedTime = getDateInMillis(unfortmatedTime).toString();
                    }

                    time = unfortmatedTime;

                    Log.d("News time is : ", time);

                }

                while((line = br.readLine()) != null){
                    para = line;
                    paragraph.add(para);
                    Log.d("News para is : ", para);

                }

                filename = file.getName();
                filename = filename.substring(0,filename.indexOf("."));
                fileContents.add(new DataHTML(Integer.parseInt(filename),title,time,link,description));

                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

            //Log.v("file name is : ", text.toString());
        }

    }

    public static CharSequence getDateInMillis(String srcDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

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
