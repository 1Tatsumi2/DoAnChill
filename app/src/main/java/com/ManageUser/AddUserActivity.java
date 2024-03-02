package com.ManageUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.MusicManager.MusicManagerActivity;
import com.example.doanchill.R;
import com.example.doanchill.UploadActivity;

public class AddUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
    }
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddUserActivity.this, ManageUserActivity.class));
        finish();
    }
}