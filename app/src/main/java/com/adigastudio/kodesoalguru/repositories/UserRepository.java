package com.adigastudio.kodesoalguru.repositories;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private FirebaseFirestore firestore;
    private String TAG = "ExamRepository";

    private void getFirestoreInstance(){
        if(firestore == null){
            firestore = FirebaseFirestore.getInstance();
        }
    }

    public void getUserDetail(FirestoreGetUserDetail firestoreGetUserDetail){
        getFirestoreInstance();
        try {
            firestore.collection("users").document(getUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = new User(
                                document.getId(),
                                document.getString("name"),
                                document.getString("email"),
                                document.getString("instance"),
                                document.getBoolean("administrator"),
                                document.getBoolean("headmaster"),
                                document.getBoolean("moderator"),
                                document.getBoolean("teacher"),
                                document.getBoolean("student"),
                                document.getString("class"),
                                document.getString("student_number"),
                                document.getTimestamp("created").toDate());
                        firestoreGetUserDetail.onCallback(user);
                    } else {
                        firestoreGetUserDetail.onCallback(null);
                        Log.d(TAG, "checkToken: not exists");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    firestoreGetUserDetail.onCallback(new User(task.getException()));
                }
            });
        } catch (Exception err) {
            firestoreGetUserDetail.onCallback(new User(err));
        }
    }

    public void changeEmail(FirestoreGetChangeEmailStatus firestoreGetChangeEmailStatus, String oldEmail, String newEmail, String password){
        getFirestoreInstance();
        try {
            FirebaseUser user = getUser();

            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updateEmail(newEmail).addOnCompleteListener(taskEmail -> {
                        if (taskEmail.isSuccessful()) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("email", newEmail);
                            firestore.collection("users").document(user.getUid())
                                    .update(data)
                                    .addOnCompleteListener(taskFirestore -> {
                                        if (taskFirestore.isSuccessful()) {
                                            firestoreGetChangeEmailStatus.onCallback(new User(newEmail));
                                        } else {
                                            firestoreGetChangeEmailStatus.onCallback(new User(taskFirestore.getException()));
                                        }
                                    });
                        } else {
                            firestoreGetChangeEmailStatus.onCallback(new User(taskEmail.getException()));
                        }
                    });

                } else {
                    firestoreGetChangeEmailStatus.onCallback(new User(task.getException()));
                }
            });
        } catch (Exception err) {
            Log.d(TAG, "changeEmail: " + err.getMessage());
            firestoreGetChangeEmailStatus.onCallback(new User(err));
        }
    }

    public void syncEmail(FirestoreGetSyncEmailStatus firestoreGetSyncEmailStatus){
        getFirestoreInstance();
        try {
            FirebaseUser user = getUser();
            Map<String, Object> data = new HashMap<>();
            data.put("email", user.getEmail());
            firestore.collection("users").document(user.getUid())
                    .update(data)
                    .addOnCompleteListener(taskFirestore -> {
                        if (taskFirestore.isSuccessful()) {
                            firestoreGetSyncEmailStatus.onCallback(new User(user.getEmail()));
                        } else {
                            firestoreGetSyncEmailStatus.onCallback(new User(taskFirestore.getException()));
                        }
                    });
        } catch (Exception err) {
            Log.d(TAG, "syncEmail: " + err.getMessage());
            firestoreGetSyncEmailStatus.onCallback(new User(err));
        }
    }

    public void changePassword(FirestoreGetChangePasswordStatus firestoreGetChangePasswordStatus, String oldPassword, String newPassword){
        getFirestoreInstance();
        try {
            FirebaseUser user = getUser();
            Log.d(TAG, "changePassword: " + user.getEmail());
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(taskAuth -> {
                        if (taskAuth.isSuccessful()) {
                            firestoreGetChangePasswordStatus.onCallback(new User(user.getEmail()));

                        } else {
                            firestoreGetChangePasswordStatus.onCallback(new User(taskAuth.getException()));
                        }
                    });

                } else {
                    firestoreGetChangePasswordStatus.onCallback(new User(task.getException()));
                }
            });
        } catch (Exception err) {
            firestoreGetChangePasswordStatus.onCallback(new User(err));
        }
    }

    private FirebaseUser getUser(){
        return new AuthRepository().getCurrentUser();
    }

    public interface FirestoreGetUserDetail {
        void onCallback(User user);
    }

    public interface FirestoreGetChangeEmailStatus {
        void onCallback(User user);
    }

    public interface FirestoreGetSyncEmailStatus {
        void onCallback(User user);
    }

    public interface FirestoreGetChangePasswordStatus {
        void onCallback(User user);
    }

}
