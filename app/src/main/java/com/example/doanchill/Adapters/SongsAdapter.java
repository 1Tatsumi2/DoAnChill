package com.example.doanchill.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Song;
import com.example.doanchill.R;

import java.util.List;

public class SongsAdapter extends ArrayAdapter<Song> {

    public SongsAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, null);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
        TextView tvDuration=convertView.findViewById(R.id.tvDuration);
        ImageView artWork=convertView.findViewById(R.id.artWork);
        Song song = getItem(position);
        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        Glide.with(getContext()).load(song.getImage()).into(artWork);
        tvDuration.setText(millisecondsToString(song.getDuration()));


        return convertView;
    }
    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}
