package com.example.noteexample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNoteBinding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(addNoteBinding.getRoot());


        addNoteBinding.numberPickerPriority.setMinValue(1);
        addNoteBinding.numberPickerPriority.setMaxValue(10);

        setSupportActionBar(addNoteBinding.toolbar);


        Intent editIntent = getIntent();
        if (editIntent.hasExtra(ID_KEY)){
            setTitle("Edit Note");
            String title = editIntent.getStringExtra(TITLE_KEY);
            String description = editIntent.getStringExtra(DESCRIPTION_KEY);
            int priority = editIntent.getIntExtra(PRIORITY_KEY, 1);
            addNoteBinding.titleEt.setText(title);
            addNoteBinding.descriptionEt.setText(description);
            addNoteBinding.numberPickerPriority.setValue(priority);
        }else{
            setTitle("Add Note");
        }
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
        int priority = addNoteBinding.numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert title and description for this note!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(TITLE_KEY, title);
        intent.putExtra(DESCRIPTION_KEY, description);
        intent.putExtra(PRIORITY_KEY, priority);

        int id = getIntent().getIntExtra(ID_KEY, -1);
        if (id!= -1){
            intent.putExtra(ID_KEY, id);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}