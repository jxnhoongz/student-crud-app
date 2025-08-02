package com.example.crud.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crud.R;
import com.example.crud.models.Student;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnStudentClickListener listener;
    private int expandedPosition = -1; // Track which item has expanded actions

    public interface OnStudentClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public StudentAdapter(List<Student> studentList, OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.bind(student, position);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    /**
     * Add a new student to the list
     */
    public void addStudent(Student student) {
        studentList.add(student);
        notifyItemInserted(studentList.size() - 1);
    }

    /**
     * Remove student at specific position
     */
    public void removeStudent(int position) {
        if (position >= 0 && position < studentList.size()) {
            studentList.remove(position);
            notifyItemRemoved(position);

            // Reset expanded position if needed
            if (expandedPosition == position) {
                expandedPosition = -1;
            } else if (expandedPosition > position) {
                expandedPosition--;
            }
        }
    }

    /**
     * Update student at specific position
     */
    public void updateStudent(int position, Student student) {
        if (position >= 0 && position < studentList.size()) {
            studentList.set(position, student);
            notifyItemChanged(position);
        }
    }

    /**
     * Get student at specific position
     */
    public Student getStudent(int position) {
        if (position >= 0 && position < studentList.size()) {
            return studentList.get(position);
        }
        return null;
    }

    /**
     * Toggle action buttons visibility for a specific item
     */
    private void toggleActions(int position) {
        int previousExpandedPosition = expandedPosition;

        if (expandedPosition == position) {
            // Collapse currently expanded item
            expandedPosition = -1;
        } else {
            // Expand new item
            expandedPosition = position;
        }

        // Notify changes
        if (previousExpandedPosition != -1) {
            notifyItemChanged(previousExpandedPosition);
        }
        if (expandedPosition != -1) {
            notifyItemChanged(expandedPosition);
        }
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvStudentId, tvStudentInitial;
        private MaterialButton btnMoreActions, btnEditStudent, btnDeleteStudent;
        private LinearLayout layoutActions;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews();
            setupClickListeners();
        }

        private void initializeViews() {
            // Text views
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvStudentInitial = itemView.findViewById(R.id.tv_student_initial);

            // Buttons and layouts
            btnMoreActions = itemView.findViewById(R.id.btn_more_actions);
            btnEditStudent = itemView.findViewById(R.id.btn_edit_student);
            btnDeleteStudent = itemView.findViewById(R.id.btn_delete_student);
            layoutActions = itemView.findViewById(R.id.layout_actions);
        }

        private void setupClickListeners() {
            // More actions button (toggle action visibility)
            btnMoreActions.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    toggleActions(position);
                }
            });

            // Edit button
            btnEditStudent.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(position);
                }
            });

            // Delete button
            btnDeleteStudent.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(position);
                }
            });

            // Item click (tap anywhere to expand actions)
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    toggleActions(position);
                }
            });
        }

        public void bind(Student student, int position) {
            // Set student information
            tvStudentName.setText(student.getName());
            tvStudentId.setText(formatStudentId(student.getId()));

            // Set student initial (first letter of name)
            String initial = getStudentInitial(student.getName());
            tvStudentInitial.setText(initial);

            // Handle action buttons visibility
            boolean isExpanded = (position == expandedPosition);
            layoutActions.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            // Simple visual feedback for more button
            btnMoreActions.setText(isExpanded ? "×" : "⋯");
        }

        /**
         * Format student ID with "ID: " prefix
         */
        private String formatStudentId(String id) {
            return "ID: " + id;
        }

        /**
         * Get the first letter of student name for initial
         */
        private String getStudentInitial(String name) {
            if (name != null && !name.isEmpty()) {
                return name.substring(0, 1).toUpperCase();
            }
            return "?";
        }
    }

    // Additional utility methods (keeping all your existing functionality)

    /**
     * Get the total number of students
     */
    public int getStudentCount() {
        return studentList.size();
    }

    /**
     * Check if the list is empty
     */
    public boolean isEmpty() {
        return studentList.isEmpty();
    }

    /**
     * Clear all students from the list
     */
    public void clearAllStudents() {
        int size = studentList.size();
        studentList.clear();
        expandedPosition = -1;
        notifyItemRangeRemoved(0, size);
    }

    /**
     * Find student by ID
     */
    public int findStudentById(String id) {
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if student ID exists
     */
    public boolean isStudentIdExists(String id) {
        return findStudentById(id) != -1;
    }

    /**
     * Get all student IDs
     */
    public String[] getAllStudentIds() {
        String[] ids = new String[studentList.size()];
        for (int i = 0; i < studentList.size(); i++) {
            ids[i] = studentList.get(i).getId();
        }
        return ids;
    }

    /**
     * Update the entire list (useful for search/filter functionality)
     */
    public void updateList(List<Student> newList) {
        this.studentList = newList;
        expandedPosition = -1;
        notifyDataSetChanged();
    }

    /**
     * Collapse any expanded actions
     */
    public void collapseAll() {
        if (expandedPosition != -1) {
            int previousExpanded = expandedPosition;
            expandedPosition = -1;
            notifyItemChanged(previousExpanded);
        }
    }
}