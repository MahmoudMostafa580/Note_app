package com.example.noteexample.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.noteexample.NoteViewModel;
import com.example.noteexample.R;
import com.example.noteexample.databinding.ActivityMainBinding;
import com.example.noteexample.pojo.Note;
import com.example.noteexample.pojo.NoteAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    NoteViewModel noteViewModel;
    ActivityResultLauncher<Intent> addLauncher;
    ActivityResultLauncher<Intent> editLauncher;
    ActivityMainBinding mainBinding;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        setSupportActionBar(mainBinding.toolbar);

        mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mainBinding.recyclerView.setHasFixedSize(true);
        mainBinding.recyclerView.setVisibility(View.INVISIBLE);
        noteAdapter = new NoteAdapter();
        mainBinding.recyclerView.setAdapter(noteAdapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (notes.isEmpty()) {
                    mainBinding.noNotesTv.setVisibility(View.VISIBLE);
                    mainBinding.recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    noteAdapter.setNotes(notes);
                    mainBinding.noNotesTv.setVisibility(View.INVISIBLE);
                    mainBinding.recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete note!")
                        .setMessage("Are you sure to delete this note?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                noteViewModel.deleteNote(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                noteViewModel.getAllNotes();
                                noteAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();

            }
        }).attachToRecyclerView(mainBinding.recyclerView);


        addLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            String title = intent.getStringExtra(AddNoteActivity.TITLE_KEY);
                            String description = intent.getStringExtra(AddNoteActivity.DESCRIPTION_KEY);
                            int priority = intent.getIntExtra(AddNoteActivity.PRIORITY_KEY,3);
                            int time = (int)System.currentTimeMillis();
                            Note note = new Note(title, description, priority, time);
                            noteViewModel.insertNote(note);
                            Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Note not saved!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mainBinding.addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                addLauncher.launch(intent);
            }
        });

        editLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            int id = result.getData().getIntExtra(AddNoteActivity.ID_KEY, -1);
                            if (id == -1) {
                                Toast.makeText(MainActivity.this, "Note can't be updated!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String title = result.getData().getStringExtra(AddNoteActivity.TITLE_KEY);
                            String description = result.getData().getStringExtra(AddNoteActivity.DESCRIPTION_KEY);
                            int priority = result.getData().getIntExtra(AddNoteActivity.PRIORITY_KEY, 1);
                            int time = result.getData().getIntExtra(AddNoteActivity.TIME_KEY,0);
                            Note note = new Note(title, description, priority,time);
                            note.setId(id);
                            noteViewModel.updateNote(note);
                            Toast.makeText(MainActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra(AddNoteActivity.ID_KEY, note.getId());
                intent.putExtra(AddNoteActivity.TITLE_KEY, note.getTitle());
                intent.putExtra(AddNoteActivity.DESCRIPTION_KEY, note.getContent());
                intent.putExtra(AddNoteActivity.PRIORITY_KEY, note.getPriority());
                intent.putExtra(AddNoteActivity.TIME_KEY, note.getTime());
                editLauncher.launch(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_note:
                deleteAllNotes();
                return true;
            case R.id.low_priority:
                noteViewModel.getAllNotesAsc().observe(MainActivity.this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        noteAdapter.setNotes(notes);
                    }
                });
                return true;
            case R.id.high_priority:
                noteViewModel.getAllNotes().observe(MainActivity.this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        noteAdapter.setNotes(notes);
                    }
                });
                return true;
            case R.id.most_recent:
                noteViewModel.getAllNotesWithTime().observe(MainActivity.this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        noteAdapter.setNotes(notes);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllNotes() {
        new AlertDialog.Builder(this)
                .setTitle("Delete all notes!")
                .setMessage("Are you ant to delete all notes?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (noteAdapter.getItemCount() == 0) {
                            Toast.makeText(MainActivity.this, "No notes to delete!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        noteViewModel.deleteAllNotes();
                        noteAdapter.notifyDataSetChanged();
                        mainBinding.noNotesTv.setVisibility(View.VISIBLE);
                        mainBinding.recyclerView.setVisibility(View.INVISIBLE);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }
}