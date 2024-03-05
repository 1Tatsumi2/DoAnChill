package com.example.doanchill.Banner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BannerDetailActivity extends AppCompatActivity {

    String imageUrl;
    ImageView image;
    Button edit;
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_detail);
        edit=findViewById(R.id.editBanner);
        image=findViewById(R.id.bannerImage);
        Intent intent=getIntent();
        Bundle extraData=intent.getExtras();
        String key=extraData.getString("key");
        DocumentReference ref=fStore.collection("banner").document(key);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Glide.with(BannerDetailActivity.this).load(documentSnapshot.getString("image")).into(image);
                imageUrl=documentSnapshot.getString("image");
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(BannerDetailActivity.this, EditBannerActivity.class);
                i.putExtra("key",key);
                i.putExtra("image",imageUrl);
                startActivity(i);
                finish();
            }
        });
    }
}