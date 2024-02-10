package com.example.doanchill.ROOM;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Music")
public class Music {
    @ColumnInfo(name = "music_id")
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "name")
    String music_name;

    @ColumnInfo(name = "image")
    String music_image;
    @ColumnInfo(name = "author_name")
    String author_name;

    @ColumnInfo(name = "singer_name")
    String singer_name;

    @ColumnInfo(name = "duration")
    int music_duration;

    @ColumnInfo(name = "path")
    String music_path;

    @ColumnInfo(name = "album",defaultValue = "")
    String album_name;

    public Music(String music_name, String author_name, String singer_name, int music_duration, String music_path, String album_name, String music_image) {
        this.music_name = music_name;
        this.author_name = author_name;
        this.singer_name = singer_name;
        this.music_duration = music_duration;
        this.music_path = music_path;
        this.album_name = album_name;
        this.id=0;
        this.music_image=music_image;
    }

    @Ignore
    public Music()
    {

    }

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public int getMusic_duration() {
        return music_duration;
    }

    public void setMusic_duration(int music_duration) {
        this.music_duration = music_duration;
    }

    public String getMusic_path() {
        return music_path;
    }

    public void setMusic_path(String music_path) {
        this.music_path = music_path;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getMusic_image() {
        return music_image;
    }

    public void setMusic_image(String music_image) {
        this.music_image = music_image;
    }
}
