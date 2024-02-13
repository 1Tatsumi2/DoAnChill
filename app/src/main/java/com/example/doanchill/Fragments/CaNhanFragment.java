package com.example.doanchill.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaNhanFragment extends Fragment {

    List<Song> songArrayList;
    ListView lvSongs;

    SongsAdapter songsAdapter;
    ValueEventListener valueEventListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ca_nhan, container, false);

        lvSongs = view.findViewById(R.id.lvSongs);

        songArrayList = new ArrayList<>();

        songsAdapter = new SongsAdapter(getActivity(), songArrayList);
        lvSongs.setAdapter(songsAdapter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance("https://chill-8ac86-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("music");
        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songArrayList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren())
                {
                    Log.d("LoiHetCuu", String.valueOf(itemSnapshot.getValue()));
                    Song song= itemSnapshot.getValue(Song.class);
                    song.setKey(itemSnapshot.getKey());
                    songArrayList.add(song);
                }
                songsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Log.d("LoiHetCuu","gettagooooo");
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songArrayList.get(position);
                Intent openMusicPlayer = new Intent(getActivity(), MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("position",position);
                startActivity(openMusicPlayer);
            }
        });

        return view;
    }


}