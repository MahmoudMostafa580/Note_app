package com.example.noteexample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteexample.R;
import com.example.noteexample.databinding.ActivityAddNoteBinding;

public class AddNoteActivity extends AppCompatActivity {
    ActivityAddNoteBinding addNoteBinding;
    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PRIORITY_KEY = "priority";
    public static final String TIME_KEY = "time";
    private ArrayAdapter<String> priorityAdapter;
    private int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNoteBinding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(addNoteBinding.getRoot());

        setSupportActionBar(addNoteBinding.toolbar);

        String[] priorities = getResources().getStringArray(R.array.priorities);

        Intent editIntent = getIntent();
        if (editIntent.hasExtra(ID_KEY)) {
            setTitle("Edit Note");
            String title = editIntent.getStringExtra(TITLE_KEY);
            String description = editIntent.getStringExtra(DESCRIPTION_KEY);
            time = editIntent.getIntExtra(TIME_KEY, 0);
            int priority = editIntent.getIntExtra(PRIORITY_KEY, 1);
            if (priority == 1) {
                addNoteBinding.priorityActv.setText(priorities[2]);
            } else if (priority == 2) {
                addNoteBinding.priorityActv.setText(priorities[1]);
            } else if (priority == 3) {
                addNoteBinding.priorityActv.setText(priorities[0]);
            }
            addNoteBinding.titleEt.setText(title);
            addNoteBinding.descriptionEt.setText(description);
        } else {
            setTitle("Add Note");
        }
        priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, priorities);
        addNoteBinding.priorityActv.setAdapter(priorityAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String title = addNoteBinding.titleLayout.getEditText().getText().toString();
        String description = addNoteBinding.descriptionLayout.getEditText().getText().toString();
        String priorityString = addNoteBinding.priorityLayout.getEditText().getText().toString();


        int priority;
        if (priorityString.equals("High Priority")) {
            priority = 3;
        } else if (priorityString.equals("Medium Priority")) {
            priority = 2;
        } else {
            priority = 1;
        }

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert title and description for this note!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(TITLE_KEY, title);
        intent.putExtra(DESCRIPTION_KEY, description);
        intent.putExtra(PRIORITY_KEY, priority);
        intent.putExtra(TIME_KEY, time);

        int id = getIntent().getIntExtra(ID_KEY, -1);
        if (id != -1) {
            intent.putExtra(ID_KEY, id);
        }
        if (time != 0) {
            intent.putExtra(TIME_KEY, time);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}