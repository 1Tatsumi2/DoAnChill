package com.example.doanchill.Models;

public class SliderModel {

    private String image;
    private String key;

    public SliderModel(String image)
    {
        this.image = image;
    }

    public SliderModel() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
