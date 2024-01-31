package com.example.doanchill.Class;

import java.io.Serializable;

public class Song implements Serializable {

    private String title;
    private String artist;
    private String path;
    private int duration;

    public Song(String title, String artist, String path,int duration) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration=duration;
    }

    public int getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
