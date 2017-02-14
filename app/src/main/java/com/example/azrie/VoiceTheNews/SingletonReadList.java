package com.example.azrie.VoiceTheNews;

import java.util.ArrayList;

/**
 * Created by Azrie on 14/11/2016.
 */

public final class SingletonReadList {

    private static final SingletonReadList SELF = new SingletonReadList();

    private ArrayList<String> readList = new ArrayList<String>();

    private SingletonReadList(){}

    public static SingletonReadList getInstance(){
        return SELF;
    }

    public ArrayList<String> getReadList(){
        return readList;
    }

}
