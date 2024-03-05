package com.MusicManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ManageUser.ManageUserActivity;
import com.ManageUser.UserDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.Playlist.AddMusicToPlayListActivity;
import com.example.doanchill.Playlist.PlaylistManagerActivity;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaylistDetailActivity extends AppCompatActivity {

    TextView numSong,name,desc,author;
    ImageView image;
    Button addMusic,deleteMusic;
    String key,imageToDelete;
    List<Song> songArrayList;
    ListView lvSongs;
    SongsAdapter songsAdapter;
    FirebaseAuth fAuth;
    String UserID,ID,role,rolePlay;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference ref;
    CollectionReference refUser=db.collection("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        numSong=findViewById(R.id.detailPlaylistNumSong);
        name=findViewById(R.id.detailPLaylistName);
        author=findViewById(R.id.detailPLaylistAuthor);
        desc=findViewById(R.id.detailPlaylistDesc);
        image=findViewById(R.id.detailPlaylistImage);
        deleteMusic=findViewById(R.id.DeletePlaylist);
        lvSongs = findViewById(R.id.lvPlaylistSong);
        addMusic=findViewById(R.id.addMusicToPlaylist);
        songArrayList = new ArrayList<>();
        songsAdapter = new SongsAdapter(this, songArrayList);
        lvSongs.setAdapter(songsAdapter);
        fAuth=FirebaseAuth.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        showAllSongs();
        Intent intent=getIntent();
        Bundle extraData=intent.getExtras();
        key=extraData.getString("key");
        imageToDelete=extraData.getString("playlist");
        DocumentReference UserRef=refUser.document(UserID);
        DocumentReference ref=db.collection("Playlist").document(key);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                documentSnapshot.getDocumentReference("author").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(Objects.equals(documentSnapshot.getString("role"), "Admin") || Objects.equals(documentSnapshot.getString("role"), "Moderator"))
                        {
                            author.setText("DoAnChill");
                        }
                        else {
                            author.setText(documentSnapshot.getString("fName"));
                        }
                        rolePlay=documentSnapshot.getString("role");
                        ID=documentSnapshot.getId();
                        if(!Objects.equals(UserID, ID))
                        {
                            addMusic.setVisibility(View.GONE);
                            UserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    role=documentSnapshot.getString("role");
                                     if((Objects.equals(rolePlay, "Admin") || Objects.equals(rolePlay, "Moderator"))&&(Objects.equals(role, "Admin") || Objects.equals(role, "Moderator")))
                                     {
                                         addMusic.setVisibility(View.VISIBLE);
                                     }
                                }
                            });
                        }
                    }
                });
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
        deleteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
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

    private void confirmDelete() {
        String[] options={"Yes","No"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Do you want to delete this playlist?");
        //set items/options
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle click
                if(which==0)
                {
                    deleteSong();
                }
                else if(which==1)
                {
                    dialog.dismiss();
                }
            }
        });
        //create/show dialog
        builder.create().show();
    }

    private void deleteSong() {
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference imageReference = storage.getReferenceFromUrl(imageToDelete);
         imageReference.delete();

        ref=db.collection("Playlist").document(key);
        ref.delete();
        Toast.makeText(PlaylistDetailActivity.this, "Delete success", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}