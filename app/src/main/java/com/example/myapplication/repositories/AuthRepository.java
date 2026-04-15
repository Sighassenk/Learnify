package com.example.myapplication.repositories;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.models.User;

public class AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AuthRepository";

    public void register(String name, String email, String password,
                         String role, OnCompleteListener<Void> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful() && authTask.getResult().getUser() != null) {
                        String uid = authTask.getResult().getUser().getUid();
                        User user = new User();
                        user.setId(uid);
                        user.setName(name);
                        user.setEmail(email);
                        user.setRole(role);

                        Log.d(TAG, "Auth successful, creating Firestore doc for UID: " + uid);
                        db.collection("users").document(uid).set(user)
                                .addOnCompleteListener(listener);
                    } else {
                        Log.e(TAG, "Auth failed during registration", authTask.getException());
                        if (listener != null) {
                            listener.onComplete((com.google.android.gms.tasks.Task<Void>) (com.google.android.gms.tasks.Task<?>) authTask);
                        }
                    }
                });
    }

    public void login(String email, String password,
                      OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void getCurrentUser(OnSuccessListener<User> listener) {
        if (auth.getCurrentUser() == null) {
            listener.onSuccess(null);
            return;
        }
        String uid = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        listener.onSuccess(doc.toObject(User.class));
                    } else {
                        Log.w(TAG, "Document missing for UID: " + uid + ". Attempting auto-repair...");
                        // Auto-repair: Create a basic profile if it's missing
                        User repairUser = new User();
                        repairUser.setId(uid);
                        repairUser.setEmail(email);
                        repairUser.setName(email != null ? email.split("@")[0] : "User");
                        repairUser.setRole("student"); // Default to student on repair

                        db.collection("users").document(uid).set(repairUser)
                                .addOnSuccessListener(aVoid -> listener.onSuccess(repairUser))
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Auto-repair failed: " + e.getMessage());
                                    listener.onSuccess(null);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore read failed: " + e.getMessage());
                    listener.onSuccess(null);
                });
    }

    public void logout() {
        auth.signOut();
    }
}
