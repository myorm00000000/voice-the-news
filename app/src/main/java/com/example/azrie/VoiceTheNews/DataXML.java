package com.example.azrie.VoiceTheNews;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Azrie on 21/7/2016.
 */
public class DataXML implements Parcelable{

    private String id;
    private String title;
    private String link;
    private String description;
    private String pubDate;
    private String thumbnailUrl;
    private boolean read;

    public DataXML(String title, String pubDate, String link, String description, boolean read){


        idGenerator(link);

        this.title = title;
        this.pubDate = pubDate;
        this.link = link;
        this.description = description;
        this.read = read;
    }

    public String getId() { return id; }

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

    public boolean getRead (){ return read; }

    public void setRead (boolean read){ this.read = read; }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void idGenerator(String link){

        this.id = link.substring(link.lastIndexOf("/") + 1);

    }


    DataXML(Parcel in){
        id = in.readString();
        title = in.readString();
        pubDate = in.readString();
        link = in.readString();
        description = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write to Parcel
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(pubDate);
        parcel.writeString(link);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<DataXML> CREATOR = new Parcelable.Creator<DataXML>() {
        public DataXML createFromParcel(Parcel in) {
            return new DataXML(in);
        }

        public DataXML[] newArray(int size) {
            return new DataXML[size];
        }
    };

}
