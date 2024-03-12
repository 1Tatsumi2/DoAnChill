package com.example.doanchill.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.MusicManager.MusicManagerActivity;
import com.MusicManager.PlaylistDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Adapters.ExploreAdapter;
import com.example.doanchill.Adapters.PlaylistMainAdapter;
import com.example.doanchill.Adapters.SliderAdapter;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.MainActivity;
import com.example.doanchill.Models.SliderModel;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.R;
import com.example.doanchill.Utils.SliderTimer;
import com.example.doanchill.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;


public class TrangChuFragment extends Fragment {

    private ViewPager slider;
    RecyclerView Playlists,Top100,TopSinger,MyPlaylist,Explore;
    PlaylistMainAdapter playlistMainAdapter,playlistTop100Adapter,getPlaylistTopSingerAdapter,myPlaylistAdapter;
    ExploreAdapter exploreAdapter;
    List<com.example.doanchill.Class.Playlist> playlistList,playlistsTop100,playlistsTopSinger,myPlaylist,explorePlaylist;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Playlist");
    CollectionReference refUser=db.collection("users");
    CollectionReference bannerRef=db.collection("banner");
    FirebaseAuth fAuth;
    String UserID;
    private List<SliderModel> sliderModelList;
    private SliderAdapter sliderAdapter;
    private TabLayout sliderIndicator;
    private Timer timer;
    ConstraintLayout layout;
    TextView story;
    ImageView imageStory, MicroIcon,buttonSearch;
    String keyStory, imagestoryl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        buttonSearch=view.findViewById(R.id.buttonSearch);
        fAuth=FirebaseAuth.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        DocumentReference UserRef=refUser.document(UserID);
        slider = view.findViewById(R.id.slider);
        sliderIndicator = view.findViewById(R.id.slider_indicator);
        Playlists=view.findViewById(R.id.LstViewPlay);
        Top100=view.findViewById(R.id.LstViewTop100);
        TopSinger=view.findViewById(R.id.LstViewTopSinger);
        MyPlaylist=view.findViewById(R.id.LstViewMyplaylist);
        Explore=view.findViewById(R.id.LstViewExplore);
        layout=view.findViewById(R.id.story);
        story=view.findViewById(R.id.artistStory);
        imageStory=view.findViewById(R.id.imageStory);
        MicroIcon = view.findViewById(R.id.microIcon);
        DocumentReference docRef=ref.document("jXSAzW14cqjibGREeCjw");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Playlist playlist=documentSnapshot.toObject(Playlist.class);
                    story.setText(playlist.getName());
                    playlist.setKey(documentSnapshot.getId());
                    keyStory=playlist.getKey();
                    imagestoryl=playlist.getImage();
                    Glide.with(getContext()).load(playlist.getImage()).into(imageStory);
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), PlaylistDetailActivity.class);
                i.putExtra("playlist",imagestoryl);
                i.putExtra("key",keyStory);
                startActivity(i);
            }
        });
        //Explore
        explorePlaylist=new ArrayList<>();
        Explore.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        exploreAdapter=new ExploreAdapter(Playlists.getContext(), explorePlaylist);
        Explore.setAdapter(exploreAdapter);
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

        //My Playlist
        myPlaylist=new ArrayList<>();
        MyPlaylist.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        myPlaylistAdapter=new PlaylistMainAdapter(Playlists.getContext(), myPlaylist);
        MyPlaylist.setAdapter(myPlaylistAdapter);


        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            if (results != null && !results.isEmpty()) {
                                String recognizedText = results.get(0);
                                Bundle bundle = new Bundle();
                                bundle.putString("search", recognizedText);
                                bundle.putBoolean("isSth",true);
                                CaNhanFragment caNhanFragment=new CaNhanFragment();
                                caNhanFragment.setArguments(bundle);
                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.getBottomNavigationView().setSelectedItemId(R.id.profile);
                                // Xử lý văn bản đã nhận dạng ở đây
                                replaceFragment(caNhanFragment);
                            }
                            // Xử lý dữ liệu trả về ở đây
                        }
                    }
                });



        MicroIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói gì đó...");
                someActivityResultLauncher.launch(intent);
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.getBottomNavigationView().setSelectedItemId(R.id.profile);
                replaceFragment(new CaNhanFragment());
            }
        });

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
                                playlist.setKey(documentSnapshot.getId());
                                playlist.getAuthor().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.getId().equals(UserID))
                                        {
                                            myPlaylist.add(playlist);
                                            myPlaylistAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                            else if (Objects.equals(documentSnapshot.getString("classified"), "Explore")) {
                                Playlist playlist=documentSnapshot.toObject(Playlist.class);
                                playlist.setKey(documentSnapshot.getId());
                                explorePlaylist.add(playlist);
                                exploreAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });
        sliderModelList = new ArrayList<>();
        sliderAdapter = new SliderAdapter(getContext(),sliderModelList);
        slider.setAdapter(sliderAdapter);
        sliderIndicator.setupWithViewPager(slider);
        bannerRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    SliderModel sliderModel=documentSnapshot.toObject(SliderModel.class);
                    sliderModelList.add(sliderModel);
                    sliderAdapter.notifyDataSetChanged();
                }
            }
        });

        timer = new Timer();



        timer.scheduleAtFixedRate(new SliderTimer(getActivity(),slider,sliderModelList.size()),4000,6000);

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}