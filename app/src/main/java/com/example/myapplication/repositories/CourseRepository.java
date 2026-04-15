package com.example.myapplication.repositories;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.myapplication.models.Course;
import com.example.myapplication.models.Section;
import com.example.myapplication.models.Lesson;
import java.util.List;

public class CourseRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Student: get all courses (browse)
    public void getAllCourses(OnSuccessListener<List<Course>> listener) {
        db.collection("courses")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Course> courses = snapshot.toObjects(Course.class);
                    listener.onSuccess(courses);
                });
    }

    // Student: get courses by category
    public void getCoursesByCategory(String category, OnSuccessListener<List<Course>> listener) {
        db.collection("courses")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(snapshot ->
                        listener.onSuccess(snapshot.toObjects(Course.class)));
    }

    // Instructor: get own courses
    public void getInstructorCourses(String instructorId, OnSuccessListener<List<Course>> listener) {
        db.collection("courses")
                .whereEqualTo("instructorId", instructorId)
                .get()
                .addOnSuccessListener(snapshot ->
                        listener.onSuccess(snapshot.toObjects(Course.class)));
    }

    // Instructor: create course
    public void createCourse(Course course, OnCompleteListener<Void> listener) {
        String id = db.collection("courses").document().getId();
        course.setId(id);
        db.collection("courses").document(id)
                .set(course)
                .addOnCompleteListener(listener);
    }

    // Get sections of a course
    public void getSections(String courseId, OnSuccessListener<List<Section>> listener) {
        db.collection("sections")
                .whereEqualTo("courseId", courseId)
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot ->
                        listener.onSuccess(snapshot.toObjects(Section.class)))
                .addOnFailureListener(e -> {
                    // Fallback if index isn't created yet
                    db.collection("sections")
                            .whereEqualTo("courseId", courseId)
                            .get()
                            .addOnSuccessListener(snapshot ->
                                    listener.onSuccess(snapshot.toObjects(Section.class)));
                });
    }

    // Get lessons of a section
    public void getLessons(String sectionId, OnSuccessListener<List<Lesson>> listener) {
        db.collection("lessons")
                .whereEqualTo("sectionId", sectionId)
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot ->
                        listener.onSuccess(snapshot.toObjects(Lesson.class)))
                .addOnFailureListener(e -> {
                    // Fallback if index isn't created yet
                    db.collection("lessons")
                            .whereEqualTo("sectionId", sectionId)
                            .get()
                            .addOnSuccessListener(snapshot ->
                                    listener.onSuccess(snapshot.toObjects(Lesson.class)));
                });
    }
    // Fetch multiple courses by a list of IDs (for My Courses screen)
    public void getCoursesByIds(List<String> ids, OnSuccessListener<List<Course>> listener) {
        if (ids == null || ids.isEmpty()) {
            listener.onSuccess(new java.util.ArrayList<>());
            return;
        }
        db.collection("courses")
                .whereIn("id", ids)
                .get()
                .addOnSuccessListener(snap -> listener.onSuccess(snap.toObjects(Course.class)));
    }
}
