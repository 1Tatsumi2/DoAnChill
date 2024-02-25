package com.MusicManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Adapters.SongsPlaylistAdapter;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaylistDetailActivity extends AppCompatActivity {

    TextView numSong,name,desc,nameSong;
    ImageView image;
    String key;
    List<Song> songArrayList;
    ListView lvSongs;
    SongsAdapter songsAdapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
//    CollectionReference ref=db.collection("Music");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        numSong=findViewById(R.id.detailPlaylistNumSong);
        name=findViewById(R.id.detailPLaylistName);
        desc=findViewById(R.id.detailPlaylistDesc);
        nameSong=findViewById(R.id.detailPlaylistSongName);
        image=findViewById(R.id.detailPlaylistImage);
        lvSongs = findViewById(R.id.lvPlaylistSong);
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
                for(Map.Entry<String,Object> entry:map.entrySet())
                {
                    DocumentReference reference=(DocumentReference) entry.getValue();
                    reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Song song=documentSnapshot.toObject(Song.class);
                            song.setKey(documentSnapshot.getId());
                            songArrayList.add(song);
                            songsAdapter.notifyDataSetChanged();
                        }
                    });
                }
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
            }
        });
    }
    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}