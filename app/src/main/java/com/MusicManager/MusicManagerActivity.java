package com.MusicManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ManageUser.ManageUserActivity;
import com.ManageUser.UserDetailActivity;
import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Song;
import com.example.doanchill.Class.Users;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.example.doanchill.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicManagerActivity extends AppCompatActivity {

    List<Song> songArrayList;
    ListView lvSongs;
    SearchView searchView;
    FloatingActionButton fab;
    SongsAdapter songsAdapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Music");
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

        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    Song song=documentSnapshot.toObject(Song.class);
                    song.setKey(documentSnapshot.getId());
                    songArrayList.add(song);
                }
                songsAdapter.notifyDataSetChanged();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MusicManagerActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
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
                Song song = songsAdapter.getItem(position);
                Intent openMusicPlayer = new Intent(MusicManagerActivity.this, MusicDetailActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("key",song.getKey());
                startActivity(openMusicPlayer);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void searchList(String text) {
        ArrayList<Song> searchLists = new ArrayList<>();
        for (Song data : songArrayList) {
            if(data.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchLists.add(data);
            }
        }
        songsAdapter.searchSongLst(searchLists);
    }

    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}