package com.example.myapplication.repositories;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.models.Enrollment;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Enroll student in a course
    public void enroll(String userId, String courseId, OnCompleteListener<Void> listener) {
        String id = db.collection("enrollments").document().getId();
        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setProgress(0f);
        enrollment.setEnrolledAt(System.currentTimeMillis());

        db.collection("enrollments").document(id)
                .set(enrollment)
                .addOnCompleteListener(listener);
    }

    // Check if user is already enrolled
    public void isEnrolled(String userId, String courseId, OnSuccessListener<Boolean> listener) {
        db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnSuccessListener(snapshot ->
                        listener.onSuccess(!snapshot.isEmpty()));
    }

    // Get all enrolled courses for a student
    public void getEnrollments(String userId, OnSuccessListener<List<Enrollment>> listener) {
        db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot ->
                        listener.onSuccess(snapshot.toObjects(Enrollment.class)));
    }

    // Mark a lesson as complete and update progress
    public void completeLesson(String enrollmentId, String lessonId,
                               int totalLessons, OnCompleteListener<Void> listener) {
        DocumentReference ref = db.collection("enrollments").document(enrollmentId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snap = transaction.get(ref);
            List<String> completed = (List<String>) snap.get("completedLessons");
            if (completed == null) completed = new ArrayList<>();

            if (!completed.contains(lessonId)) {
                completed.add(lessonId);
                float progress = (float) completed.size() / totalLessons;
                transaction.update(ref, "completedLessons", completed);
                transaction.update(ref, "progress", progress);
            }
            return null;
        }).addOnCompleteListener(task -> {
            if (listener != null) {
                // Cast Task<Object> to Task<Void> to match listener type
                listener.onComplete((com.google.android.gms.tasks.Task<Void>) (com.google.android.gms.tasks.Task<?>) task);
            }
        });
    }
    // Get a single enrollment by userId + courseId
    public void getEnrollmentByUserAndCourse(String userId, String courseId,
                                             OnSuccessListener<Enrollment> listener) {
        db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        listener.onSuccess(snap.getDocuments().get(0).toObject(Enrollment.class));
                    } else {
                        listener.onSuccess(null);
                    }
                });
    }
}
