package com.example.myapplication.repositories;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.myapplication.models.Lesson;
import com.example.myapplication.utils.FirebaseUtils;

import java.util.List;

public class LessonRepository {

    private final FirebaseFirestore db = FirebaseUtils.getDb();

    public void addLesson(Lesson lesson, OnCompleteListener<Void> listener) {
        String id = db.collection("lessons").document().getId();
        lesson.setId(id);
        db.collection("lessons").document(id)
                .set(lesson)
                .addOnCompleteListener(listener);
    }

    public void getLessonsBySection(String sectionId, OnSuccessListener<List<Lesson>> listener) {
        db.collection("lessons")
                .whereEqualTo("sectionId", sectionId)
                .orderBy("order")
                .get()
                .addOnSuccessListener(snap -> listener.onSuccess(snap.toObjects(Lesson.class)));
    }

    public void getLessonsByCourse(String courseId, OnSuccessListener<List<Lesson>> listener) {
        db.collection("lessons")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnSuccessListener(snap -> listener.onSuccess(snap.toObjects(Lesson.class)));
    }

    public void updateLesson(Lesson lesson, OnCompleteListener<Void> listener) {
        db.collection("lessons").document(lesson.getId())
                .set(lesson)
                .addOnCompleteListener(listener);
    }

    public void deleteLesson(String lessonId, OnCompleteListener<Void> listener) {
        db.collection("lessons").document(lessonId)
                .delete()
                .addOnCompleteListener(listener);
    }
}
