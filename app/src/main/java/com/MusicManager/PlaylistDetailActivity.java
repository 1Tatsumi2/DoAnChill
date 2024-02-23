package com.MusicManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PlaylistDetailActivity extends AppCompatActivity {

    TextView numSong,name,desc,nameSong;
    ImageView image;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference ref=db.collection("Playlist").document("dVBUTyd4zkRfJTgtbeWJ");;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        numSong=findViewById(R.id.detailPlaylistNumSong);
        name=findViewById(R.id.detailPLaylistName);
        desc=findViewById(R.id.detailPlaylistDesc);
        nameSong=findViewById(R.id.detailPlaylistSongName);
        image=findViewById(R.id.detailPlaylistImage);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Playlist playlist=documentSnapshot.toObject(Playlist.class);
//                numSong.setText(playlist.getSongNumber());
//                name.setText(playlist.getName());
//                desc.setText(playlist.getDescription());
//                Glide.with(PlaylistDetailActivity.this).load(playlist.getImage()).into(image);

                DocumentReference songRef=documentSnapshot.getDocumentReference("song");
                songRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        nameSong.setText(documentSnapshot.getString("title"));
                    }
                });
            }
        });
    }
}