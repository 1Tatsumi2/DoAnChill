package com.example.doanchill.Fragments;

import static android.content.Intent.getIntent;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Intent;

import com.example.doanchill.Class.Song;
import com.example.doanchill.Interface.MusicService;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_SubMain extends Fragment {
    Song currentSong;
    FrameLayout layout;
    Bundle songExtraData;
    TextView tvTime, tvTitle, tvArtist;
    TextView tvDuration;
    int position,currentPos;
    ImageView nextBtn, previousBtn,back, btnShuffle,btnLoop;
    CircleImageView tvImage;
    SeekBar seekBarTime;
    SeekBar seekBarVolume;
    Button btnPlay;
    String key;
    ImageButton dotbutton;
    static MediaPlayer mMediaPlayer;
    ArrayList<Song> musicList;
    NotificationManager notificationManager;
    MusicService musicService;
    MediaSessionCompat mediaSession;
    AdView mAdView;
    ObjectAnimator objectAnimator;
    String currentImageUri,UserID;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();

    Song song;
    private boolean isShuffleOn = false;
    private boolean isLoopOn = false;
    private ArrayList<Integer> playedSongs = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__sub_main, container, false);
        return view;
    }



}