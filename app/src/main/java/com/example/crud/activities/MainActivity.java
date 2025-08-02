package com.example.crud.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud.R;
import com.example.crud.adapters.StudentAdapter;
import com.example.crud.models.Student;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI Components - Updated to match new layout
    private TextInputLayout tilStudentName, tilStudentId;
    private TextInputEditText etStudentName, etStudentId;
    private MaterialButton btnAddStudent;
    private RecyclerView rvStudents;
    private TextView tvStudentCount;
    private LinearLayout layoutEmptyState;

    // Data and Adapter
    private StudentAdapter adapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        updateEmptyState();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        // Input fields
        tilStudentName = findViewById(R.id.til_student_name);
        tilStudentId = findViewById(R.id.til_student_id);
        etStudentName = findViewById(R.id.et_student_name);
        etStudentId = findViewById(R.id.et_student_id);

        // Buttons and other views
        btnAddStudent = findViewById(R.id.btn_add_student);
        rvStudents = findViewById(R.id.rv_students);
        tvStudentCount = findViewById(R.id.tv_student_count);
        layoutEmptyState = findViewById(R.id.layout_empty_state);

        // Initialize data
        studentList = new ArrayList<>();
    }

    /**
     * Setup RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        adapter = new StudentAdapter(studentList, new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onEditClick(int position) {
                showEditStudentDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                showDeleteConfirmationDialog(position);
            }
        });

        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);

        // Add item decoration for better spacing (optional)
        // rvStudents.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    /**
     * Setup click listeners for UI components
     */
    private void setupClickListeners() {
        btnAddStudent.setOnClickListener(v -> addStudent());

        // Optional: Add keyboard "Done" action support
        etStudentId.setOnEditorActionListener((v, actionId, event) -> {
            addStudent();
            return true;
        });
    }

    /**
     * Add a new student to the list
     */
    private void addStudent() {
        String name = etStudentName.getText().toString().trim();
        String id = etStudentId.getText().toString().trim();

        // Clear any previous errors
        tilStudentName.setError(null);
        tilStudentId.setError(null);

        // Validate input
        if (!validateInput(name, id)) {
            return;
        }

        // Check for duplicate ID
        if (isStudentIdExists(id)) {
            tilStudentId.setError("Student ID already exists");
            etStudentId.requestFocus();
            return;
        }

        // Create and add student
        Student student = new Student(name, id);
        studentList.add(student);
        adapter.notifyItemInserted(studentList.size() - 1);

        // Clear input fields
        clearInputFields();

        // Update UI
        updateStudentCount();
        updateEmptyState();

        // Show success message
        showSnackbar("Student added successfully", false);

        // Scroll to newly added item
        rvStudents.smoothScrollToPosition(studentList.size() - 1);
    }

    /**
     * Validate user input
     */
    private boolean validateInput(String name, String id) {
        boolean isValid = true;

        if (name.isEmpty()) {
            tilStudentName.setError("Student name is required");
            etStudentName.requestFocus();
            isValid = false;
        } else if (name.length() < 2) {
            tilStudentName.setError("Name must be at least 2 characters");
            etStudentName.requestFocus();
            isValid = false;
        }

        if (id.isEmpty()) {
            tilStudentId.setError("Student ID is required");
            if (isValid) etStudentId.requestFocus();
            isValid = false;
        } else if (!id.matches("\\d+")) {
            tilStudentId.setError("Student ID must contain only numbers");
            if (isValid) etStudentId.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    /**
     * Check if student ID already exists
     */
    private boolean isStudentIdExists(String id) {
        for (Student student : studentList) {
            if (student.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clear input fields and remove focus
     */
    private void clearInputFields() {
        etStudentName.setText("");
        etStudentId.setText("");
        etStudentName.clearFocus();
        etStudentId.clearFocus();

        // Clear any errors
        tilStudentName.setError(null);
        tilStudentId.setError(null);
    }

    /**
     * Show edit student dialog
     */
    private void showEditStudentDialog(int position) {
        Student student = studentList.get(position);

        // Create input fields for dialog
        TextInputLayout tilName = new TextInputLayout(this);
        TextInputEditText etName = new TextInputEditText(this);
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etName.setText(student.getName());
        tilName.addView(etName);
        tilName.setHint("Student Name");

        TextInputLayout tilId = new TextInputLayout(this);
        TextInputEditText etId = new TextInputEditText(this);
        etId.setInputType(InputType.TYPE_CLASS_NUMBER);
        etId.setText(student.getId());
        tilId.addView(etId);
        tilId.setHint("Student ID");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);
        layout.addView(tilName);
        layout.addView(tilId);

        new AlertDialog.Builder(this)
                .setTitle("Edit Student")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newId = etId.getText().toString().trim();

                    if (validateEditInput(newName, newId, position)) {
                        student.setName(newName);
                        student.setId(newId);
                        adapter.notifyItemChanged(position);
                        showSnackbar("Student updated successfully", false);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Validate input for editing (similar to add but allows same ID if it's the same student)
     */
    private boolean validateEditInput(String name, String id, int currentPosition) {
        if (name.isEmpty() || name.length() < 2) {
            showSnackbar("Invalid name. Name must be at least 2 characters.", true);
            return false;
        }

        if (id.isEmpty() || !id.matches("\\d+")) {
            showSnackbar("Invalid ID. ID must contain only numbers.", true);
            return false;
        }

        // Check if ID exists (but allow same student to keep their ID)
        for (int i = 0; i < studentList.size(); i++) {
            if (i != currentPosition && studentList.get(i).getId().equals(id)) {
                showSnackbar("Student ID already exists", true);
                return false;
            }
        }

        return true;
    }

    /**
     * Show delete confirmation dialog
     */
    private void showDeleteConfirmationDialog(int position) {
        Student student = studentList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Store student info for undo functionality
                    Student deletedStudent = studentList.get(position);

                    // Remove student
                    studentList.remove(position);
                    adapter.notifyItemRemoved(position);

                    // Update UI
                    updateStudentCount();
                    updateEmptyState();

                    // Show snackbar with undo option
                    showUndoSnackbar(deletedStudent, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Show snackbar with undo option for delete
     */
    private void showUndoSnackbar(Student deletedStudent, int position) {
        Snackbar.make(findViewById(android.R.id.content),
                        deletedStudent.getName() + " deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> {
                    // Restore student
                    int insertPosition = Math.min(position, studentList.size());
                    studentList.add(insertPosition, deletedStudent);
                    adapter.notifyItemInserted(insertPosition);
                    updateStudentCount();
                    updateEmptyState();
                    showSnackbar("Student restored", false);
                })
                .show();
    }

    /**
     * Update the student count display
     */
    private void updateStudentCount() {
        tvStudentCount.setText(String.valueOf(studentList.size()));
    }

    /**
     * Update empty state visibility
     */
    private void updateEmptyState() {
        if (studentList.isEmpty()) {
            rvStudents.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvStudents.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Show snackbar message
     */
    private void showSnackbar(String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message,
                isError ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT);

        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(R.color.error_color, getTheme()));
        }

        snackbar.show();
    }

    /**
     * Optional: Add method to handle back button press
     */
    @Override
    public void onBackPressed() {
        if (studentList.size() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit? You have " + studentList.size() + " students in your list.")
                    .setPositiveButton("Exit", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Stay", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}