package com.example.doanchill;

import static com.example.doanchill.ApplicationClass.ACTION_NEXTS;
import static com.example.doanchill.ApplicationClass.ACTION_PLAY;
import static com.example.doanchill.ApplicationClass.ACTION_PREV;
import static com.example.doanchill.ApplicationClass.CHANNEL_ID_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Button;
import android.media.MediaPlayer;
import android.view.View;
import android.os.Handler;
import android.widget.SeekBar.OnSeekBarChangeListener;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.doanchill.Class.Song;
import com.example.doanchill.Interface.ActionPlaying;
import com.example.doanchill.Interface.MusicService;
import com.example.doanchill.Interface.NotificationReceiver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MusicPlayerActivity extends AppCompatActivity implements ActionPlaying,ServiceConnection {
    // views declaration
    private static final int REQUEST_PERMISSION = 99;
    Bundle songExtraData;
    TextView tvTime, tvTitle, tvArtist;
    TextView tvDuration;
    int position;
    ImageView nextBtn, previousBtn;
    SeekBar seekBarTime;
    SeekBar seekBarVolume;
    Button btnPlay;
    static MediaPlayer mMediaPlayer;
    ArrayList<Song> musicList;
    NotificationManager notificationManager;
    MusicService musicService;
    MediaSessionCompat mediaSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Song song = (Song) getIntent().getSerializableExtra("song");

        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        btnPlay = findViewById(R.id.btnPlay);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        mediaSession=new MediaSessionCompat(this,"PlayerAudio");
        if(mMediaPlayer!=null)
        {
            mMediaPlayer.stop();
        }

        //getting values from previous activity
        Intent intent = getIntent();
        songExtraData = intent.getExtras();
        musicList = (ArrayList)songExtraData.getParcelableArrayList("musics");
        position = songExtraData.getInt("position", 0);

        initializeMusicPlayer(position);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClicked();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClicked();
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevClicked();
            }
        });
    }//end main


    private void initializeMusicPlayer(int position) {
        // if mediaplayer is not null and playing reset it at the launch of activity

        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        // getting out the song name
        String name = musicList.get(position).getTitle();
        tvTitle.setText(name);
        String artist=musicList.get(position).getArtist();
        String duration = millisecondsToString(musicList.get(position).getDuration());
        tvDuration.setText(duration);
        // accessing the songs on storage

        Uri uri = Uri.parse(musicList.get(position).getPath());

        // creating a mediaplayer
        // passing the uri

        mMediaPlayer = MediaPlayer.create(this, uri);

        // SETTING ON PREPARED MEDIAPLAYER

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                // seekbar
                seekBarTime.setMax(mMediaPlayer.getDuration());

                // while mediaplayer is playing the play button should display pause
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                // start the mediaplayer
                showNotification(R.drawable.ic_pause,0F);
                mMediaPlayer.start();
            }
        });

        // setting the oncompletion listener
        // after song finishes what should happen // for now we will just set the pause button to play

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackgroundResource(R.drawable.ic_play);
            }
        });

        //volume bar
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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

        // if you want the the mediaplayer to go to next song after its finished playing one song its optional
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackgroundResource(R.drawable.ic_play);

                int currentPosition = position;
                if (currentPosition < musicList.size() -1) {
                    currentPosition++;
                } else {
                    currentPosition = 0;
                }
                initializeMusicPlayer(currentPosition);

            }
        });


        // working on seekbar

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
        seekBarTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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
    }


    //chinh thoi gian
    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intentSer=new Intent(this,MusicService.class);
        bindService(intentSer,this,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder=(MusicService.MyBinder)service;
        musicService= binder.getService();
        musicService.setCallback(MusicPlayerActivity.this);
        Log.e("Connected",musicService+ "");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService=null;
        Log.e("Disconnected",musicService+ "");
    }
    public void showNotification(int playPauseBtn, Float playbackSpeed)
    {
        Intent intent=new Intent(this,MusicPlayerActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE);
        Intent prevIntent=new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent=PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Intent playIntent=new Intent(this,NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent=PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Intent nextIntent=new Intent(this,NotificationReceiver.class).setAction(ACTION_NEXTS);
        PendingIntent nextPendingIntent=PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Bitmap picture= BitmapFactory.decodeResource(getResources(),R.drawable.img);
        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_music_note)
                .setLargeIcon(picture)
                .setContentTitle(musicList.get(position).getTitle())
                .setContentText(musicList.get(position).getArtist())
                .addAction(R.drawable.ic_baseline_skip_previous_24,"Previous",prevPendingIntent)
                .addAction(playPauseBtn,"Play",playPendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next_24,"Next",nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
//        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,musicList.get(position).getDuration())
//                .build());
//        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
//                .setState(PlaybackStateCompat.STATE_PLAYING,mMediaPlayer.getCurrentPosition(),playbackSpeed)
//                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
//                .build());
    }

    @Override
    public void nextClicked() {
        if(position<musicList.size()-1)
        {
            position++;
        }
        else {
            position=0;
        }
        initializeMusicPlayer(position);
        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            showNotification(R.drawable.ic_play,0F);
        } else {
            showNotification(R.drawable.ic_pause,0F);
        }
    }

    @Override
    public void prevClicked() {
        if(position<=0){
            position=musicList.size()-1;
        }
        else {
            position--;
        }
        initializeMusicPlayer(position);
        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            showNotification(R.drawable.ic_play,0F);
        } else {
            showNotification(R.drawable.ic_pause,0F);
        }
    }

    @Override
    public void playClicked() {
        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            // change the image of playpause button to play when we pause it
            btnPlay.setBackgroundResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_play,0F);
        } else {
            mMediaPlayer.start();
            // if mediaplayer is playing // the image of play button should display pause
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            showNotification(R.drawable.ic_pause,0F);
        }
    }
}


