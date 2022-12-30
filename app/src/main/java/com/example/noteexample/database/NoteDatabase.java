package com.example.noteexample.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.noteexample.pojo.Note;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase INSTANCE;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, NoteDatabase.class, "note_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}
