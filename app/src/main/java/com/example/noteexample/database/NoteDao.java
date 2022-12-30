package com.example.noteexample.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.noteexample.pojo.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("delete from note_table")
    void deleteAllNotes();

    @Query("select * from note_table order by priority desc")
    LiveData<List<Note>> getAllNotes();
}
