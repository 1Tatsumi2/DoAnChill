package com.example.doanchill.Playlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.MusicManager.PlaylistDetailActivity;
import com.example.doanchill.Adapters.PlaylistAdapter;
import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaylistManagerActivity extends AppCompatActivity {

    List<Playlist> playlistArrayList;
    ListView lvPlaylist;
    SearchView searchView;
    FloatingActionButton fab;
    PlaylistAdapter playlistAdapter;
    FirebaseAuth fAuth;
    String UserID,role;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Playlist");
    CollectionReference refUser=db.collection("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_manager);
        lvPlaylist=findViewById(R.id.lvPlaylists);
        fab=findViewById(R.id.fabAddPlaylist);
        searchView=findViewById(R.id.searchPlaylist);
        playlistArrayList=new ArrayList<>();
        playlistAdapter=new PlaylistAdapter(this,2,playlistArrayList);
        lvPlaylist.setAdapter(playlistAdapter);
        fAuth=FirebaseAuth.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        DocumentReference UserRef=refUser.document(UserID);
        UserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                role=documentSnapshot.getString("role");
                showPlaylist();
                if(Objects.equals(role, "User"))
                {
                    ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                            {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.setKey(documentSnapshot.getId());
                                playlist.getAuthor().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.getId().equals(UserID))
                                        {
                                            playlistArrayList.add(playlist);
                                            playlistAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
                else {
                    ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                            {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.setKey(documentSnapshot.getId());
                                playlist.getAuthor().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(Objects.equals(documentSnapshot.getString("role"), "Admin") || Objects.equals(documentSnapshot.getString("role"), "Moderator"))
                                        {
                                            playlistArrayList.add(playlist);
                                            playlistAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }                }
                    });
                }
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
                    showPlaylist();
                } else {
                    searchList(newText);
                }
                return true;
            }
        });
        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist playlist=playlistAdapter.getItem(position);
                Intent i=new Intent(PlaylistManagerActivity.this, PlaylistDetailActivity.class);
                i.putExtra("key",playlist.getKey());
                startActivity(i);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaylistManagerActivity.this,AddPlaylistActivity.class));
            }
        });
    }
    public void searchList(String text) {
        ArrayList<Playlist> searchList = new ArrayList<>();
        for (Playlist data : playlistArrayList) {
            if(data.getName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(data);
            }
        }
        playlistAdapter.searchPlaylist(searchList);
    }
    public void showPlaylist() {
        playlistAdapter.searchPlaylist((ArrayList<Playlist>) playlistArrayList);
    }
}