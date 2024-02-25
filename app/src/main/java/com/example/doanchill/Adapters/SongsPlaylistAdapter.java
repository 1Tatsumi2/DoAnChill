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

import java.util.ArrayList;
import java.util.List;

public class SongsPlaylistAdapter extends ArrayAdapter<Song> {

    private List<Song> songList;

    public SongsPlaylistAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
        this.songList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_song, null);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitlePlaylist);
        TextView tvArtist = convertView.findViewById(R.id.tvArtistPlaylist);
        TextView tvDuration=convertView.findViewById(R.id.tvDurationPlaylist);
        ImageView artWork=convertView.findViewById(R.id.artWorkPlaylist);
        Song song = getItem(position);
        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getSinger());
        Glide.with(getContext()).load(song.getImage()).into(artWork);
        tvDuration.setText(millisecondsToString(song.getDuration()));

        return convertView;
    }

    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    public void searchSongLst(ArrayList<Song> searchList)
    {
        songList.clear();
        songList.addAll(searchList);
        notifyDataSetChanged();
    }
}
