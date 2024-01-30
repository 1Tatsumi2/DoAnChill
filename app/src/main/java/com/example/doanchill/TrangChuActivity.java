package com.example.doanchill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.doanchill.Fragments.CaNhanFragment;
import com.example.doanchill.Fragments.SettingsFragment;
import com.example.doanchill.R;
import com.example.doanchill.Fragments.TrangChuFragment;
import com.example.doanchill.databinding.ActivityMainBinding;
import com.example.doanchill.databinding.ActivityTrangchuBinding;

public class TrangChuActivity extends AppCompatActivity {

    ActivityTrangchuBinding binding;
    FrameLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrangchuBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        fragmentHolder = findViewById(R.id.main_frame_layout);
        replaceFragment(new TrangChuFragment());


        binding.bottomView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new TrangChuFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new CaNhanFragment());
            } else if (item.getItemId() == R.id.settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;
        });



//        setContentView(binding.getRoot());
//        binding = ActivityTrangchuBinding.inflate(getLayoutInflater());
//        replaceFragment(new TrangChuFragment());
//
//        binding.bottomView.setOnItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.home) {
//                replaceFragment(new TrangChuFragment());
//            } else if (item.getItemId() == R.id.profile) {
//                replaceFragment(new CaNhanFragment());
//            } else if (item.getItemId() == R.id.settings) {
//                replaceFragment(new SettingsFragment());
//            }
//            return true;
//        });
//
//    }


    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}