package com.example.doanchill;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.example.doanchill.Fragments.CaNhanFragment;
import com.example.doanchill.Fragments.SettingUserFragment;
import com.example.doanchill.Fragments.SettingsFragment;
import com.example.doanchill.Fragments.TrangChuFragment;
import com.example.doanchill.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FrameLayout fragmentHolder;
    Button Login;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserID,role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fStore.collection("users").document(UserID);
        ListenerRegistration registration =documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                role=value.getString("role");
            }
        });
        setContentView(binding.getRoot());
        replaceFragment(new TrangChuFragment());
        binding.bottomView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new TrangChuFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new CaNhanFragment());
            } else if ((item.getItemId() == R.id.settings) && ((Objects.equals(role, "Admin")) ||Objects.equals(role,"Moderator"))) {
                replaceFragment(new SettingsFragment());
            } else if ((item.getItemId() == R.id.settings) && Objects.equals(role, "User")) {
                replaceFragment(new SettingUserFragment());
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}