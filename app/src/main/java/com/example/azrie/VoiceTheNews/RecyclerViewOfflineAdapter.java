package com.example.azrie.VoiceTheNews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Azrie on 01/9/2016.
 */
public class RecyclerViewOfflineAdapter extends RecyclerView.Adapter<RecyclerViewOfflineAdapter.ViewHolder>{

    ArrayList<DataHTML> fileContents;
    RecyclerView recyclerView;
    Context context;

    public RecyclerViewOfflineAdapter(Context context, ArrayList<DataHTML> fileContents, RecyclerView recyclerView){

        Collections.sort(fileContents, new MyComparator());

        this.context = context;
        this.fileContents = fileContents;
        this.recyclerView = recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView text_title;
        TextView text_description;
        TextView text_date;
        View view;
        String currentLink;


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
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context,"Clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(),OfflineActivity.class);
                Bundle bundle = new Bundle();

                bundle.putParcelableArrayList("link",fileContents);
                bundle.putInt("position",holder.getLayoutPosition());

                intent.putExtra("bundle",bundle);
                view.getContext().startActivity(intent);


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataHTML current = fileContents.get(position);

        holder.text_title.setText(current.getTitle());
        holder.text_description.setText(current.getDescription());
        holder.text_date.setText(current.getPubDate());

        holder.currentLink = current.getLink();
    }

    @Override
    public int getItemCount() {
        return fileContents.size();
    }
}


class MyComparator implements Comparator<DataHTML> {
    @Override
    public int compare(DataHTML c1, DataHTML c2) {
        if(c1.getFilename() > c2.getFilename())
            return -1;
        else if(c1.getFilename() < c2.getFilename())
            return 1;
        return 0;
    }
}