package com.example.doanchill.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ManageUser.ManageUserActivity;
import com.MusicManager.MusicManagerActivity;
import com.SettingAcc.SettingAccActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.MusicPlayerActivity;
import com.example.doanchill.Playlist.AddPlaylistActivity;
import com.example.doanchill.Playlist.PlaylistManagerActivity;
import com.example.doanchill.R;
import com.example.doanchill.SignInActivity;
import com.example.doanchill.UploadActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {
    Button Logout;
    AppCompatButton musicBtn;
    AppCompatButton logOut,settingAcc,manageUser,managePlaylist;
    CircleImageView image;
    TextView name, email;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false);
        name=view.findViewById(R.id.nameProfile);
        email=view.findViewById(R.id.emailProfile);
        musicBtn=view.findViewById(R.id.manageMusic);
        logOut=view.findViewById(R.id.Logout);
        settingAcc=view.findViewById(R.id.AdminAccount);
        image=view.findViewById(R.id.imageView5);
        manageUser=view.findViewById(R.id.ManageUser);
        managePlaylist=view.findViewById(R.id.ManagePlaylist);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        UserID=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("users").document(UserID);
        ListenerRegistration registration =documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    name.setText(value.getString("fName"));
                    email.setText(value.getString("email"));
                    Glide.with(getActivity()).load(value.getString("image")).into(image);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration.remove();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));
            }
        });
        settingAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingAccActivity.class));
            }
        });
        manageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ManageUserActivity.class));
            }
        });
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), MusicManagerActivity.class);
                startActivity(i);
            }
        });
        managePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), PlaylistManagerActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}