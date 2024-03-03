package com.example.doanchill.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.doanchill.Adapters.PlaylistMainAdapter;
import com.example.doanchill.Adapters.SliderAdapter;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.Models.SliderModel;
import com.example.doanchill.R;
import com.example.doanchill.Utils.SliderTimer;
import com.example.doanchill.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;


public class TrangChuFragment extends Fragment {
;;
    private ViewPager slider;
    RecyclerView Playlists,Top100,TopSinger;
    PlaylistMainAdapter playlistMainAdapter,playlistTop100Adapter,getPlaylistTopSingerAdapter;
    List<com.example.doanchill.Class.Playlist> playlistList,playlistsTop100,playlistsTopSinger;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Playlist");
    CollectionReference refUser=db.collection("users");
    FirebaseAuth fAuth;
    String UserID;
    private ArrayList<SliderModel> sliderModelList;
    private SliderAdapter sliderAdapter;
    private TabLayout sliderIndicator;
    private Timer timer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        fAuth=FirebaseAuth.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        DocumentReference UserRef=refUser.document(UserID);
        slider = view.findViewById(R.id.slider);
        sliderIndicator = view.findViewById(R.id.slider_indicator);
        Playlists=view.findViewById(R.id.LstViewPlay);
        Top100=view.findViewById(R.id.LstViewTop100);
        TopSinger=view.findViewById(R.id.LstViewTopSinger);
        //Playlist
        playlistList=new ArrayList<>();
        Playlists.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        playlistMainAdapter=new PlaylistMainAdapter(Playlists.getContext(), playlistList);
        Playlists.setAdapter(playlistMainAdapter);

        //Top 100
        playlistsTop100=new ArrayList<>();
        Top100.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        playlistTop100Adapter=new PlaylistMainAdapter(Playlists.getContext(), playlistsTop100);
        Top100.setAdapter(playlistTop100Adapter);

        //Top Singer
        playlistsTopSinger=new ArrayList<>();
        TopSinger.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        getPlaylistTopSingerAdapter=new PlaylistMainAdapter(Playlists.getContext(), playlistsTopSinger);
        TopSinger.setAdapter(getPlaylistTopSingerAdapter);

        UserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String id=documentSnapshot.getId();
                ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        playlistList.clear();
                        playlistsTop100.clear();
                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                        {
                            if(Objects.equals(documentSnapshot.getString("classified"), "Playlist"))
                            {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.setKey(documentSnapshot.getId());
                                playlistList.add(playlist);
                                playlistMainAdapter.notifyDataSetChanged();
                            } else if (Objects.equals(documentSnapshot.getString("classified"), "Top 100")) {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.setKey(documentSnapshot.getId());
                                playlistsTop100.add(playlist);
                                playlistTop100Adapter.notifyDataSetChanged();
                            } else if (Objects.equals(documentSnapshot.getString("classified"), "Top Singer")) {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.setKey(documentSnapshot.getId());
                                playlistsTopSinger.add(playlist);
                                getPlaylistTopSingerAdapter.notifyDataSetChanged();
                            }
                            else if (Objects.equals(documentSnapshot.getString("classified"), "My Playlist"))
                            {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.getAuthor().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.getId().equals(UserID))
                                        {
                                            Log.d("SOSS",UserID );
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

        sliderModelList = new ArrayList<>();
        timer = new Timer();

        sliderModelList.add(new SliderModel(R.drawable.karik,"Playlist #1"));
        sliderModelList.add(new SliderModel(R.drawable.tlinh,"Playlist #2"));
        sliderModelList.add(new SliderModel(R.drawable.binz,"Playlist #3"));
        sliderModelList.add(new SliderModel(R.drawable.poster4,"Playlist #4"));

        sliderAdapter = new SliderAdapter(getContext(),sliderModelList);
        slider.setAdapter(sliderAdapter);
        sliderIndicator.setupWithViewPager(slider);

        timer.scheduleAtFixedRate(new SliderTimer(getActivity(),slider,sliderModelList.size()),4000,6000);

        return view;
    }
}