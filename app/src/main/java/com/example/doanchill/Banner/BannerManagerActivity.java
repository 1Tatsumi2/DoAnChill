package com.example.doanchill.Banner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.doanchill.Adapters.BannerAdapter;
import com.example.doanchill.Adapters.UserAdapter;
import com.example.doanchill.Class.Users;
import com.example.doanchill.Models.SliderModel;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BannerManagerActivity extends AppCompatActivity {


    List<SliderModel> bannerList;
    ListView lvBanners;
    BannerAdapter bannerAdapter;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_manager);
        lvBanners=findViewById(R.id.lvBanners);
        bannerList=new ArrayList<>();
        bannerAdapter=new BannerAdapter(this,6,bannerList);
        lvBanners.setAdapter(bannerAdapter);
        showAllBanner();
        fStore=FirebaseFirestore.getInstance();
        CollectionReference collection=fStore.collection("banner");
        collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    SliderModel sliderModel=documentSnapshot.toObject(SliderModel.class);
                    sliderModel.setKey(documentSnapshot.getId());
                    bannerList.add(sliderModel);
                }
                bannerAdapter.notifyDataSetChanged();
            }
        });
        lvBanners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SliderModel sliderModel=bannerAdapter.getItem(position);
                Intent i=new Intent(BannerManagerActivity.this, BannerDetailActivity.class);
                i.putExtra("key",sliderModel.getKey());
                startActivity(i);
                finish();
            }
        });
    }

    private void showAllBanner() {
        bannerAdapter.searchLst((ArrayList<SliderModel>) bannerList);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}