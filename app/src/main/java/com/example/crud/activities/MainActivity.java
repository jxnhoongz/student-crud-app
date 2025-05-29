package com.example.crud.activities;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud.R;
import com.example.crud.adapters.StudentAdapter;
import com.example.crud.models.Student;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextId;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        editTextId = findViewById(R.id.editTextId);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerView);

        studentList = new ArrayList<>();

        adapter = new StudentAdapter(studentList, new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onEditClick(int position) {
                Student student = studentList.get(position);

                EditText nameInput = new EditText(MainActivity.this);
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameInput.setText(student.getName());

                EditText idInput = new EditText(MainActivity.this);
                idInput.setInputType(InputType.TYPE_CLASS_TEXT);
                idInput.setText(student.getId());

                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 30, 50, 10);
                layout.addView(nameInput);
                layout.addView(idInput);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edit Student")
                        .setView(layout)
                        .setPositiveButton("Save", (dialog, which) -> {
                            student.setName(nameInput.getText().toString());
                            student.setId(idInput.getText().toString());
                            adapter.notifyItemChanged(position);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onDeleteClick(int position) {
                studentList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String id = editTextId.getText().toString().trim();

            if (!name.isEmpty() && !id.isEmpty()) {
                Student student = new Student(name, id);
                adapter.addStudent(student);
                editTextName.setText("");
                editTextId.setText("");
            }
        });
    }
}
