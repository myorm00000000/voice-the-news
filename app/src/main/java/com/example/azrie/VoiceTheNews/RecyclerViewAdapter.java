package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Azrie on 06/8/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    ArrayList<DataXML> dataXML;
    Context context;
    String newsCase;

    public RecyclerViewAdapter(Context context, ArrayList<DataXML> dataXML, String newsCase){
        this.context = context;
        this.dataXML = dataXML;
        this.newsCase = newsCase;
        //this.recyclerView = recyclerView;

        /*for(int i = 0; i < dataXML.size(); i++){

            Log.d("News at Position " + i ," / News Title : " + dataXML.get(i).getTitle() + "Read Status is: " + dataXML.get(i).getRead() + " / Adapter ");
        }*/

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View view;
        TextView text_title;
        TextView text_description;
        TextView text_date;
        String current_link;

        public ViewHolder(View itemView) {
            super(itemView);

            text_title = (TextView) itemView.findViewById(R.id.title_text_r);
            text_description = (TextView) itemView.findViewById(R.id.title_description_r);
            text_date = (TextView) itemView.findViewById(R.id.date_text_r);


            view = itemView;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_card,parent,false);
        //final ViewHolder holder = new ViewHolder(view);
        final RecyclerViewAdapter.ViewHolder holder = new RecyclerViewAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataXML.get(holder.getLayoutPosition()).setRead(true);

                /*for(int i = 0; i < dataXML.size(); i++){
                    Log.d("News " + i + " ", " "+ dataXML.get(i).getRead() );
                }*/

                Intent intent = new Intent(view.getContext(),ActivityNewsWeb.class);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("link", dataXML);
                bundle.putInt("position",holder.getLayoutPosition());

                intent.putExtra("bundle",bundle);
                view.getContext().startActivity(intent);


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DataXML current = dataXML.get(position);

        holder.text_title.setText(current.getTitle());
        holder.text_description.setText(current.getDescription());
        holder.text_date.setText(current.getPubDate());
        holder.current_link = current.getLink();

       // Log .d("News Read Outside " + position ,"" + current.getRead() + " / News Title : " + current.getTitle());
        if(!dataXML.get(position).getRead()){
            //Log .d("News Not Read " + position ,"" + current.getRead() + " / News Title : " + current.getTitle());
            holder.text_title.setTextColor(Color.parseColor("#000000"));
            holder.text_description.setTextColor(Color.parseColor("#000000"));
            holder.text_date.setTextColor(Color.parseColor("#000000"));
        }
        else
        {
            //Log .d("News Read " + position ,"" + current.getRead() + " / News Title : " + current.getTitle());
            holder.text_title.setTextColor(Color.parseColor("#BAC1C1"));
            holder.text_description.setTextColor(Color.parseColor("#BAC1C1"));
            holder.text_date.setTextColor(Color.parseColor("#BAC1C1"));
        }

    }

    @Override
    public int getItemCount() {
        return dataXML.size();
    }
}


