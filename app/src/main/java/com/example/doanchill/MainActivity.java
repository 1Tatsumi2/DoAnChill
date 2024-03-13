package com.example.doanchill;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.example.doanchill.Fragments.CaNhanFragment;
import com.example.doanchill.Fragments.Fragment_SubMain;
import com.example.doanchill.Fragments.SettingUserFragment;
import com.example.doanchill.Fragments.SettingsFragment;
import com.example.doanchill.Fragments.TrangChuFragment;
import com.example.doanchill.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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
    AdView adView;
    InterstitialAd mInterstitialAd;
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
        reSupportFragment(new Fragment_SubMain());
        binding.bottomView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                registration.remove();
                replaceFragment(new TrangChuFragment());
            } else if (item.getItemId() == R.id.profile) {
                registration.remove();
                replaceFragment(new CaNhanFragment());
            } else if ((item.getItemId() == R.id.settings) && ((Objects.equals(role, "Admin")) ||Objects.equals(role,"Moderator"))) {
                registration.remove();
                replaceFragment(new SettingsFragment());
            } else if ((item.getItemId() == R.id.settings) && Objects.equals(role, "User")) {
                registration.remove();
                replaceFragment(new SettingUserFragment());
            }
            return true;
        });


            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

                }
            });

            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(this, getString(R.string.inter_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("Error", loadAdError.toString());
                }


                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd;

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            mInterstitialAd = null;
                        }
                    });
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mInterstitialAd != null)
                    mInterstitialAd.show(MainActivity.this);
                    else
                        Log.e("AdPending", "Ad is not ready yet!");

                }
            },10000 );
    }

    private void reSupportFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.sup_Main_framelayout, fragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}