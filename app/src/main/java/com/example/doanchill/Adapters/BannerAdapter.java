package com.example.doanchill.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Users;
import com.example.doanchill.Models.SliderModel;
import com.example.doanchill.R;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends ArrayAdapter<SliderModel> {
    private List<SliderModel> bannerList;
    public BannerAdapter(@NonNull Context context, int resource, @NonNull List<SliderModel> objects) {
        super(context, resource, objects);
        this.bannerList=new ArrayList<>(objects);
    }
    @Nullable
    @Override
    public SliderModel getItem(int position) {
        return bannerList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, null);
        ImageView imageView=convertView.findViewById(R.id.bannerItem);
        SliderModel sliderModel=getItem(position);
        Glide.with(getContext()).load(sliderModel.getImage()).into(imageView);
        return  convertView;
    }
    @Override
    public int getCount() {
        return bannerList.size();
    }
    public void searchLst(ArrayList<SliderModel> searchList)
    {
        bannerList = searchList;
        notifyDataSetChanged();
    }
}
