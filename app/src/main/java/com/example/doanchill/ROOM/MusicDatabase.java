package com.example.doanchill.ROOM;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Music.class},version = 1)
public abstract class MusicDatabase extends RoomDatabase {
    public abstract MusicDAO getMusicDAO();
}
