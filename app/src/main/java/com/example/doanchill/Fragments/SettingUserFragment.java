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
import android.widget.TextView;

import com.SettingAcc.SettingAccActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Playlist.PlaylistManagerActivity;
import com.example.doanchill.R;
import com.example.doanchill.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingUserFragment extends Fragment {
    Button Logout;
    AppCompatButton musicBtn;
    AppCompatButton logOut,settingAcc;
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
        View view=inflater.inflate(R.layout.fragment_setting_user, container, false);
        name=view.findViewById(R.id.nameUserProfile);
        email=view.findViewById(R.id.emailUserProfile);
        logOut=view.findViewById(R.id.LogoutUser);
        settingAcc=view.findViewById(R.id.UserAccount);
        musicBtn=view.findViewById(R.id.MyPlaylist);
        image=view.findViewById(R.id.profileUserImage);
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
                registration.remove();
                startActivity(new Intent(getActivity(), SettingAccActivity.class));
            }
        });
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration.remove();
                Intent i=new Intent(getActivity(), PlaylistManagerActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}