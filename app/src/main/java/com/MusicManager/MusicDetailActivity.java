package com.MusicManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ManageUser.ManageUserActivity;
import com.ManageUser.UserDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Song;
import com.example.doanchill.Fragments.SettingsFragment;
import com.example.doanchill.Interface.MusicService;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MusicDetailActivity extends AppCompatActivity {

    Bundle songExtraData;
    String key;
    TextView tvTime, tvTitle, tvArtist,tvSinger;
    TextView tvDuration;
    Button editBtn,deleteBtn;
    int position;
    ImageView tvImage;
    SeekBar seekBarTime;
    SeekBar seekBarVolume;
    static MediaPlayer mMediaPlayer;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference ref;
    Song song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        song = (Song) getIntent().getSerializableExtra("song");
        editBtn=findViewById(R.id.editMusic);
        deleteBtn=findViewById(R.id.deleteMusic);
        tvTime = findViewById(R.id.manageTime);
        tvImage=findViewById(R.id.manageImage);
        tvDuration = findViewById(R.id.manageDuration);
        tvSinger=findViewById(R.id.manageSinger);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        tvTitle = findViewById(R.id.manageTitle);
        tvArtist = findViewById(R.id.manageArtist);
        if(mMediaPlayer!=null)
        {
            mMediaPlayer.stop();
        }

        //getting values from previous activity
        Intent intent = getIntent();
        songExtraData = intent.getExtras();
        key=songExtraData.getString("key");


        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }
        // getting out the song name
        String name = song.getTitle();
        tvTitle.setText(name);
        String singer=song.getSinger();
        String duration = millisecondsToString(song.getDuration());
        Glide.with(this).load(song.getImage()).into(tvImage);
        tvDuration.setText(duration);
        String artist=song.getArtist();
        tvSinger.setText(singer);
        tvArtist.setText(artist);
        Uri uri = Uri.parse(song.getPath());
        mMediaPlayer = MediaPlayer.create(this, uri);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                // seekbar
                seekBarTime.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.setLooping(true);
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                // Handle progress change
                // You can use 'progress' to get the current progress value
                // tang giam am luong
                float volume = (float) progress / 100f;
                mMediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle the start of tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle the stop of tracking touch
            }
        });
        // thanh thoi gian bai hat
        seekBarTime.setMax(mMediaPlayer.getDuration());
        Handler handler = new Handler();
        // Update seek bar and time TextView periodically
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer.isPlaying()) {
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    seekBarTime.setProgress(currentPosition);
                    tvTime.setText(millisecondsToString(currentPosition));
                }
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        }, 0);
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    tvTime.setText(millisecondsToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle the start of tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle the stop of tracking touch
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                Intent intent=new Intent(MusicDetailActivity.this, UpdateActivity.class)
                        .putExtra("Title",song.getTitle())
                        .putExtra("Artist",song.getArtist())
                        .putExtra("Singer",song.getSinger())
                        .putExtra("Album",song.getAlbum())
                        .putExtra("Image",song.getImage())
                        .putExtra("Audio",song.getPath())
                        .putExtra("Key",song.getKey())
                        .putExtra("Duration",mMediaPlayer.getDuration());
                mMediaPlayer.stop();
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMediaPlayer.stop();
        startActivity(new Intent(MusicDetailActivity.this, MusicManagerActivity.class));
        finish();
    }

    private void confirmDelete() {
        String[] options={"Yes","No"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Do you want to delete this song?");
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
        StorageReference audioReference = storage.getReferenceFromUrl(song.getPath());
        StorageReference imageReference = storage.getReferenceFromUrl(song.getImage());

        Task<Void> deleteAudio = audioReference.delete();
        Task<Void> deleteImage = imageReference.delete();

        deleteAudio.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return deleteImage;
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ref=db.collection("Music").document(key);
                //xóa file audio và image cũ(not done)
                ref.delete();
                Toast.makeText(MusicDetailActivity.this, "Delete success", Toast.LENGTH_SHORT).show();
                mMediaPlayer.stop();
                startActivity(new Intent(MusicDetailActivity.this, MusicManagerActivity.class));
                finish();
            }
        });
    }


    //chinh thoi gian
    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}