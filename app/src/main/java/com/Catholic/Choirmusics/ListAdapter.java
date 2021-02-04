package com.Catholic.Choirmusics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.cardview.widget.CardView;

public class ListAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private final List<Song> songlist;
    private final List<Song> filteredList;

    public ListAdapter(Context context, List<Song> songlist) {
        this.context = context;
        this.songlist = songlist;
        filteredList = new ArrayList<>();
        filteredList.addAll(songlist);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredList.get(i); // return Song item
    }

    @Override
    public long getItemId(int i) {
        return i;  //TODO change this to return song id
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.songs_list_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .build();

        Song song = filteredList.get(i);

        Picasso.get().load(song.getImageUrl()).transform(transformation).into(viewHolder.thumbnail);
        viewHolder.songName.setText(song.getSongName());
        viewHolder.artistName.setText(song.getSongArtist());
        viewHolder.songDuration.setText(song.getSongDuration());
        return view;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering (CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (songlist != null && constraint != null) {
                    filteredList.clear();
                    List<Song> newValues = new ArrayList<>();
                    for (Song song: songlist) {
                        if(constraint.length() == 0) {
                            newValues.add(song); //TODO change this if you want empty list
                        }else{
                            if(song.getSongName().toLowerCase().contains(constraint.toString().toLowerCase()))  {
                                newValues.add(song);
                            }
                        }
                    }
                    filteredList.addAll(newValues);
                    //filteredList = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults (CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }



    private static class ViewHolder{
        TextView songName;
        TextView artistName;
        TextView songDuration;
        ImageView thumbnail;
        CardView cardView;
        ImageView currentlyPlaying;

        ViewHolder(View view){
            songName = view.findViewById(R.id.songName);
            thumbnail = view.findViewById(R.id.songThumbnail);
            artistName = view.findViewById(R.id.artistName);
            songDuration = view.findViewById(R.id.songDuration);
            cardView = view.findViewById(R.id.cardView);
            currentlyPlaying = view.findViewById(R.id.currentlyPlaying);
        }
    }
}