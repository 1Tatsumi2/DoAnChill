package com.example.doanchill.Class;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Map;

public class Library implements Serializable {
    private DocumentReference user;
    private Map<String, Object> songs;
    private int songNumber;

    public Library() {
    }

    public Library(DocumentReference user, Map<String, Object> songs, int songNumber) {
        this.user = user;
        this.songs = songs;
        this.songNumber = songNumber;
    }

    public Library(DocumentReference user, int songNumber) {
        this.user = user;
        this.songNumber = songNumber;
    }

    public DocumentReference getUser() {
        return user;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }

    public Map<String, Object> getSongs() {
        return songs;
    }

    public void setSongs(Map<String, Object> songs) {
        this.songs = songs;
    }
}
