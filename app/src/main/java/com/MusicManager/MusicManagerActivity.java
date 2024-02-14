package com.MusicManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.example.doanchill.UploadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicManagerActivity extends AppCompatActivity {

    List<Song> songArrayList;
    ListView lvSongs;
    SearchView searchView;
    FloatingActionButton fab;
    SongsAdapter songsAdapter;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_manager);
        lvSongs = findViewById(R.id.lvSongs);
        fab=findViewById(R.id.fab);
        searchView = findViewById(R.id.search);

        songArrayList = new ArrayList<>();

        songsAdapter = new SongsAdapter(this, songArrayList);
        lvSongs.setAdapter(songsAdapter);
        showAllSongs();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://chill-8ac86-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("music");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songArrayList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Song song = itemSnapshot.getValue(Song.class);
                    song.setKey(itemSnapshot.getKey());
                    songArrayList.add(song);
                }
                songsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MusicManagerActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showAllSongs();
                } else {
                    searchList(newText);
                }
                return true;
            }
        });

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songArrayList.get(position);
                Intent openMusicPlayer = new Intent(MusicManagerActivity.this, MusicDetailActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("position", position);
                startActivity(openMusicPlayer);
            }
        });
    }
    public void searchList(String text) {
        ArrayList<Song> searchList = new ArrayList<>();
        for (Song data : songArrayList) {
            if(data.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    data.getSinger().toLowerCase().contains(text.toLowerCase()) ||
                    data.getArtist().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(data);
            }
        }
        songsAdapter.searchSongLst(searchList);
    }

    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}