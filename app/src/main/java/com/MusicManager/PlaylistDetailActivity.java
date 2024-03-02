package com.MusicManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ManageUser.ManageUserActivity;
import com.ManageUser.UserDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.Playlist.AddMusicToPlayListActivity;
import com.example.doanchill.Playlist.PlaylistManagerActivity;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaylistDetailActivity extends AppCompatActivity {

    TextView numSong,name,desc;
    ImageView image;
    Button addMusic;
    String key;
    List<Song> songArrayList;
    ListView lvSongs;
    SongsAdapter songsAdapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        numSong=findViewById(R.id.detailPlaylistNumSong);
        name=findViewById(R.id.detailPLaylistName);
        desc=findViewById(R.id.detailPlaylistDesc);
        image=findViewById(R.id.detailPlaylistImage);
        lvSongs = findViewById(R.id.lvPlaylistSong);
        addMusic=findViewById(R.id.addMusicToPlaylist);
        songArrayList = new ArrayList<>();
        songsAdapter = new SongsAdapter(this, songArrayList);
        lvSongs.setAdapter(songsAdapter);
        showAllSongs();
        Intent intent=getIntent();
        Bundle extraData=intent.getExtras();
        key=extraData.getString("key");
        DocumentReference ref=db.collection("Playlist").document(key);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                name.setText(documentSnapshot.getString("name"));
                desc.setText(documentSnapshot.getString("description"));
                Double k=documentSnapshot.getDouble("songNumber");
                Integer ik=k.intValue();
                String tk=ik.toString();
                numSong.setText(tk + " songs");
                Glide.with(PlaylistDetailActivity.this).load(documentSnapshot.getString("image")).into(image);
                Map<String,Object> map=(Map<String,Object>)documentSnapshot.get("songs");
                if(map != null) {
                    for(Map.Entry<String,Object> entry:map.entrySet())
                    {
                        DocumentReference reference=(DocumentReference) entry.getValue();
                        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                               if(documentSnapshot.exists())
                               {
                                   Song song=documentSnapshot.toObject(Song.class);
                                   song.setKey(documentSnapshot.getId());
                                   songArrayList.add(song);
                                   songsAdapter.notifyDataSetChanged();
                               }
                               else {
                                   ref.update("songs."+entry.getKey(), FieldValue.delete());
                               }
                            }
                        });
                    }
                }
            }
        });
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PlaylistDetailActivity.this, AddMusicToPlayListActivity.class);
                i.putExtra("keyPlaylist",key);
                startActivity(i);
                finish();
            }
        });
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songArrayList.get(position);
                Intent openMusicPlayer = new Intent(PlaylistDetailActivity.this, MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("key",song.getKey());
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("position", position);
                startActivity(openMusicPlayer);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PlaylistDetailActivity.this, PlaylistManagerActivity.class));
        finish();
    }
    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}