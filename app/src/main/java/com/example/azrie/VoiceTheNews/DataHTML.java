package com.example.azrie.VoiceTheNews;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Azrie on 21/7/2016.
 */
public class DataHTML implements Parcelable{

    private int filename;
    private String title;
    private String link;
    private String description;
    private String pubDate;
    private String thumbnailUrl;
    private ArrayList<String> paragraph;

    public DataHTML(int filename , String title, String pubDate, String link, String description /*ArrayList<String> paragraph*/){

        this.filename = filename;
        this.title = title;
        this.pubDate = pubDate;
        this.link = link;
        this.description = description;
        //this.paragraph = paragraph;
    }

    public int getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public ArrayList<String> getParagraph (){
        return paragraph;
    }
    public void setParagraph (ArrayList<String> paragraph){
        this.paragraph = paragraph;
    }


    DataHTML(Parcel in){
        filename = in.readInt();
        title = in.readString();
        pubDate = in.readString();
        link = in.readString();
        description = in.readString();
        paragraph = in.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write to Parcel
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(filename);
        parcel.writeString(title);
        parcel.writeString(pubDate);
        parcel.writeString(link);
        parcel.writeString(description);
        parcel.writeList(paragraph);
    }

    public static final Creator<DataHTML> CREATOR = new Creator<DataHTML>() {
        public DataHTML createFromParcel(Parcel in) {
            return new DataHTML(in);
        }

        public DataHTML[] newArray(int size) {
            return new DataHTML[size];
        }
    };

}
