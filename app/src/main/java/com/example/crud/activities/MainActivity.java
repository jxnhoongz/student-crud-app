package com.example.crud.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
        adapter = new StudentAdapter(studentList);
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
