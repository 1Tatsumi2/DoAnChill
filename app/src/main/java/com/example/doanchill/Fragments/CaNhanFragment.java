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
import androidx.appcompat.widget.SearchView;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CaNhanFragment extends Fragment {

    List<Song> songArrayList;
    ListView lvSongs;
    SearchView searchView;

    SongsAdapter songsAdapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Music");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ca_nhan, container, false);

        lvSongs = view.findViewById(R.id.lvSongs);
        searchView=view.findViewById(R.id.search);

        songArrayList = new ArrayList<>();

        songsAdapter = new SongsAdapter(getActivity(), songArrayList);
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
                int originalPosition = songArrayList.indexOf(song);
                Intent openMusicPlayer = new Intent(getActivity(), MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("key",song.getKey());
                openMusicPlayer.putExtra("position",originalPosition);
                startActivity(openMusicPlayer);
            }
        });

        return view;
    }
    public void searchList(String text)
    {
        ArrayList<Song> searchList=new ArrayList<>();
        for(Song data:songArrayList)
        {
            if(data.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    data.getSinger().toLowerCase().contains(text.toLowerCase()) ||
                    data.getArtist().toLowerCase().contains(text.toLowerCase()))
            {
                searchList.add(data);
            }
        }
        songsAdapter.searchSongLst(searchList);
    }
    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }


}