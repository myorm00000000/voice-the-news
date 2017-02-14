package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;


public class ActivityMainR extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ArrayList<DataXML> newsList = new ArrayList <DataXML>();
    private ArrayList<String> readList = new ArrayList <String>();

    private String address = "http://malaysiakini.com/en/news.rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent("finish_activity");
        sendBroadcast(intent);

        SharedPreferences languagePreference = PreferenceManager.getDefaultSharedPreferences(this);
        String test = languagePreference.getString("language","DEFAULT");

        Log.d("Preference Language:",test);

        Bundle language = getIntent().getBundleExtra("bundleAddress");

        if(test != null) {
            //address = language.getString("newsUrl");
            switch (test) {
                case "EN":
                    address = "http://www.malaysiakini.com/en/news.rss";
                    break;
                case "BM":
                    address = "http://www.malaysiakini.com/my/news.rss";
                    break;
                case "CN":
                    address = "http://www.malaysiakini.com/cn/news.rss";
                    break;
            }
        }

        setContentView(R.layout.activity_main_recent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Recent New");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        navigationView.setCheckedItem(R.id.nav_recent_news);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadArray(this,readList);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();

        if(!newsList.isEmpty()){                                                                    // Check empty on recentList -> Contain news details retrieve from XML

            setExecuteReadHTML(newsList);

            if(!SingletonReadList.getInstance().getReadList().isEmpty()){                           // Retrieve Read News from singleton Class

                for(int i = 0; i < SingletonReadList.getInstance().getReadList().size(); i++){      // Loop until Singleton size

                    String newsID = SingletonReadList.getInstance().getReadList().get(i);

                    for(int j = 0; j < newsList.size(); j++){

                        if(newsList.get(j).getId().equals(newsID) && !newsList.get(j).getRead()){   // If ID in newslist == ID in singleton && newslist at # status is unread

                            newsList.get(j).setRead(true);                                          // Set newslist status at # to read
                        }
                    }
                }
            }

            adapter.notifyDataSetChanged();                                                         // Check for data changes then updated UI

            for(int i = 0; i < newsList.size(); i++) {

                if(newsList.get(i).getRead() && !readList.contains(newsList.get(i).getId())){       // Check IF read value == TRUE / Check IF readList contains news ID at ( i ) from recentList

                    readList.add(newsList.get(i).getId());                                          // Add news ID at ( i ) from recentList to readList
                }
            }
        }

        if(!readList.isEmpty()) {                                                                   // Check empty on readList -> Contain news ID that has been read

            saveArray(readList);                                                                    // Call saveArray -> readList value is saved into memory
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            super.onBackPressed();
        }
    }

    /**
     * Supporting Method Section
     */

    public void filterReadNews(ArrayList<DataXML> list) {                                           // Remove read news from list by inserting a dummy list similar to the newslist

        final int size = list.size();
        int batchCount = 0;                                                                         // continuous # of items that are being removed

        for(int i = size - 1; i>= 0; i--) {
            if (list.get(i).getRead()) {
                list.remove(i);
                batchCount ++;
            }

            else if (batchCount != 0) { // dispatch batch
                adapter.notifyItemRangeRemoved(i + 1, batchCount);                                  // Locate the read news then remove from recyclerview
                batchCount = 0;
            }
        }
        // notify for remaining
        if (batchCount != 0) { // dispatch remaining
           adapter.notifyItemRangeRemoved(0, batchCount);
        }
    }


    public void setNewsList(ArrayList<DataXML> list, String newsType){

        this.newsList = list;
        this.adapter = new RecyclerViewAdapter(this, list, newsType);
        this.recyclerView.setAdapter(adapter);
    }

    // Method to save arraylist persistently
    public boolean saveArray(ArrayList<String> list){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putInt("Status_size", list.size());

        for(int i = 0; i < list.size();i++){

            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, list.get(i));
        }

        return mEdit1.commit();
    }

    // Method to retrieve persistently saved arraylist
    public static void loadArray(Context context, ArrayList<String> list){

        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(context);
        list.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for(int i=0;i<size;i++) {

            list.add(mSharedPreference1.getString("Status_" + i, null));
        }
    }

    public ArrayList<DataXML> setReadList(ArrayList<DataXML> list){                                 //Method to set read value to TRUE

        if(!readList.isEmpty() && !list.isEmpty()){

            for(int i = 0; i < this.readList.size(); i++){

                String newsId = readList.get(i);

                for(int j = 0; j < list.size(); j++){

                    if(list.get(j).getId().equals(newsId)){

                        list.get(j).setRead(true);
                    }
                }
            }
        }

        return list;
    }

    public void setExecuteReadHTML(ArrayList<DataXML> list){                                        //Method to execute BackgroundProcessHTML

        Log.d("List Size",""+ list.size());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(preferences.getBoolean("autoDownloadPreference",true)){
            for(int i = 0; i < list.size(); i++) {
                Log.d("List Size",""+list.size());
                new BackgroundProcessHTML(getApplicationContext(),list.get(i).getLink()).execute();
            }
        }

        else{
            Toast.makeText(this,"Access folder permission required", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigation Drawer Code Section
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recent_news) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle("Recent News");

            new BackgroundProcessXML(ActivityMainR.this,this,address).execute();
        }

        else if (id == R.id.nav_unread_news) {

            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle("Unread News");

            new UnreadNewsProcess(ActivityMainR.this,this,newsList,address).execute();


        }

        else if (id == R.id.nav_downloaded_news) {
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle("Downloaded News");

            new ReadTextFile(ActivityMainR.this, recyclerView).execute();
        }

        else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, ActivitySettingPreferance.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
