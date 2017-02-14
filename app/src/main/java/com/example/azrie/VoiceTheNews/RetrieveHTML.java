package com.example.azrie.VoiceTheNews;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Azrie on 07/9/2016.
 */


class RetrieveHTML extends AsyncTask<Void,Void,Void> {
    private String address;
    private ArrayList<String> paragraphHTML;
    //public AsyncResponse delegate = null;

    public RetrieveHTML(String address){
        this.address = address;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        jSoup(address);

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        //delegate.processFinish(getParagraphHTML());
    }

    public ArrayList<String> getParagraphHTML(){
        return paragraphHTML;
    }

    public void jSoup(String address){
        paragraphHTML = new ArrayList<String>();
        try{
            Document document = Jsoup.connect(address).timeout(0).get();

            Elements title = document.select("meta[property=og:title]");
            paragraphHTML.add(title.attr("content"));

            int count = 0;
            Elements select = document.select("div[id=article_content] > p");
            Element tempParagraph;
            for (int i = 0; i < select.size(); i++){
                tempParagraph = select.get(i);
                paragraphHTML.add(tempParagraph.text());
                Log.d("Check:",""+paragraphHTML.get(count));
            }

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
}