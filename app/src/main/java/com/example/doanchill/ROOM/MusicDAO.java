package com.example.doanchill.ROOM;

import android.app.Person;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MusicDAO {
    @Insert
    public void addMusic(Music music);

    @Update
    public void updateMusic(Music music);

    @Delete
    public void deleteMusic(Music music);

    @Query("select * from Music")
    public List<Music> getAllMusic();

    @Query("select * from Music where name like :name")
    public List<Music> searchMusic(String name);
}
