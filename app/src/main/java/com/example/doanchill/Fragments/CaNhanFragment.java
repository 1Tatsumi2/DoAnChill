package com.example.doanchill.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;

import java.util.ArrayList;

public class CaNhanFragment extends Fragment {


    private static final int REQUEST_PERMISSION = 99;

    ArrayList<Song> songArrayList;
    ListView lvSongs;

    SongsAdapter songsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ca_nhan, container, false);

        lvSongs = view.findViewById(R.id.lvSongs);

        songArrayList = new ArrayList<>();

        songsAdapter = new SongsAdapter(getActivity(), songArrayList);
        lvSongs.setAdapter(songsAdapter);

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},REQUEST_PERMISSION);
            return view;
        } else {
            getSongs();
        }

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songArrayList.get(position);
                Intent openMusicPlayer = new Intent(getActivity(), MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics",songArrayList);
                openMusicPlayer.putExtra("position",position);
                startActivity(openMusicPlayer);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getSongs() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songUri, null, null,null,null);
        if(songCursor != null && songCursor.moveToFirst()) {

            int indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int indexDuration=songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String title = songCursor.getString(indexTitle);
                String artist = songCursor.getString(indexArtist);
                String path = songCursor.getString(indexData);
                int duration= Integer.parseInt(songCursor.getString(indexDuration));
                Song song = new Song(title, artist, path,duration);
                songArrayList.add(new Song(title, artist, path,duration));
            } while (songCursor.moveToNext());
        }

        songsAdapter.notifyDataSetChanged();
    }
}