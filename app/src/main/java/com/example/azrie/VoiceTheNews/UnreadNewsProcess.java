package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Azrie on 13/11/2016.
 */

public class UnreadNewsProcess extends AsyncTask <Void, Void, Void> {

    private ActivityMainR activityMain;

    private Context context;
    private String address;
    private ArrayList<DataXML> unreadNewsList; //

    public UnreadNewsProcess(ActivityMainR activityMain, Context context, ArrayList<DataXML> unreadNewsList, String address){

        this.activityMain = activityMain;
        this.unreadNewsList = unreadNewsList;
        this.context = context;
        this.address = address;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        for(int i = 0; i < unreadNewsList.size(); i++){
            if(unreadNewsList.get(i).getRead()){
                Log.d("News at " + i," Read is : " + unreadNewsList.get(i).getRead());
                //unreadNewsList.remove(i);
            }
        }

        //activityMain.setNewasdsUnreadList(unreadNewsList);
        //activityMain.setNewsUnreadList(unreadNewsList,"recent");
        activityMain.filterReadNews(unreadNewsList);
    }
}
