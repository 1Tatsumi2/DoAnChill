package com.example.doanchill.Utils;

import android.app.Activity;

import androidx.viewpager.widget.ViewPager;

import java.util.TimerTask;

public class SliderTimer extends TimerTask
{
    private Activity activity;
    private ViewPager slider;
    private int size;

    public SliderTimer(Activity activity, ViewPager slider, int size){
        this.activity = activity;
        this.slider = slider;
        this.size = size;
    }
    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(slider.getCurrentItem() < size){
                    slider.setCurrentItem(slider.getCurrentItem()+1);
                } else {
                    slider.setCurrentItem(0);
                }
            }
        });

    }
}
