package com.example.doanchill.Library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.SettingAcc.SettingAccActivity;
import com.example.doanchill.Adapters.SongsAdapter;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.Class.Users;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LibraryActivity extends AppCompatActivity {

    List<Song> songArrayList;
    ListView lvLibrary;
    ImageView back;
    Button phatNgauNhien;
    TextView soBaiHat;
    SongsAdapter songsAdapter;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("library");
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        lvLibrary=findViewById(R.id.lvLibrary);
        back=findViewById(R.id.btnBack1);
        phatNgauNhien=findViewById(R.id.phatNgauNhienLibrary);
        soBaiHat=findViewById(R.id.soBaiHatTrongThuVien);
        //FilterStart
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Sort by","Song name (A-Z)", "Artist name (A-Z)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        //FilterEnd

        songArrayList = new ArrayList<>();

        songsAdapter = new SongsAdapter(LibraryActivity.this, songArrayList);
        lvLibrary.setAdapter(songsAdapter);
        key=fAuth.getCurrentUser().getUid();
        showAllSongs();
        DocumentReference songRef=ref.document(key);
        songRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Double k=documentSnapshot.getDouble("songNumber");
                Integer ik=k.intValue();
                String tk=ik.toString();
                soBaiHat.setText(tk);
                Map<String, Object> songs = (Map<String, Object>) documentSnapshot.get("songs");
                if(songs!=null)
                {
                    for(Map.Entry<String,Object> entry:songs.entrySet())
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
                                    songRef.update("songs."+entry.getKey(), FieldValue.delete());
                                }
                            }
                        });
                    }
                }
            }
        });


        lvLibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songsAdapter.getItem(position);
                int originalPosition = songArrayList.indexOf(song);
                Intent openMusicPlayer = new Intent(LibraryActivity.this, MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("key",song.getKey());
                openMusicPlayer.putExtra("position",originalPosition);
                startActivity(openMusicPlayer);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phatNgauNhien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Xu Ly Phat Ngau Nhien
                Random random=new Random();
                int randomNum=random.nextInt(songArrayList.size());
                Song song = songsAdapter.getItem(randomNum);
                int originalPosition = songArrayList.indexOf(song);
                Intent openMusicPlayer = new Intent(LibraryActivity.this, MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("key",song.getKey());
                openMusicPlayer.putExtra("position",originalPosition);
                startActivity(openMusicPlayer);
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Xử lý item được chọn
                if(selectedItem.equals("Sort by"))
                {
                    showAllSongs();
                }
                else
                {
                    searchList(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi item không được chọn
            }
        });

    }
    public void searchList(String text) {
        ArrayList<Song> searchLists = new ArrayList<>();
        for (Song data : songArrayList) {
            searchLists.add(data);
        }

        if(Objects.equals(text, "Song name (A-Z)"))
        {
            searchLists.sort(new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    return s1.getTitle().compareTo(s2.getTitle());
                }
            });
        }
        else if(Objects.equals(text, "Artist name (A-Z)"))
        {
            searchLists.sort(new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    return s1.getArtist().compareTo(s2.getArtist());
                }
            });
        }

        songsAdapter.searchSongLst(searchLists);
    }
    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}