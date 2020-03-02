package com.adigastudio.kodesoalguru.repositories;

import android.util.Log;

import com.adigastudio.kodesoalguru.models.User;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private FirebaseFirestore firestore;
    private String TAG = "UserRepository";

    private void getFirestoreInstance(){
        if(firestore == null){
            firestore = FirebaseFirestore.getInstance();
        }
    }

    public void getUserDetail(FirestoreGetUserDetail firestoreGetUserDetail){
        getFirestoreInstance();
        if (getUser() == null) {
            NullPointerException nullPointerException = new NullPointerException("Pengguna tidak ditemukan");
            Crashlytics.logException(nullPointerException);
            firestoreGetUserDetail.onCallback(new User(nullPointerException));
            return;
        }
        firestore.collection("users").document(getUser().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null) {
                    NullPointerException nullPointerException = new NullPointerException("Pengguna tidak ditemukan");
                    Crashlytics.logException(nullPointerException);
                    firestoreGetUserDetail.onCallback(new User(nullPointerException));
                    return;
                }
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
                }
            } else {
                firestoreGetUserDetail.onCallback(new User(task.getException()));
            }
        });
    }

    public void changeEmail(FirestoreGetChangeEmailStatus firestoreGetChangeEmailStatus, String oldEmail, String newEmail, String password){
        getFirestoreInstance();
        FirebaseUser user = getUser();
        if (user == null) {
            NullPointerException nullPointerException = new NullPointerException("Pengguna tidak ditemukan");
            Crashlytics.logException(nullPointerException);
            firestoreGetChangeEmailStatus.onCallback(new User(nullPointerException));
            return;
        }
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
    }

    public void syncEmail(FirestoreGetSyncEmailStatus firestoreGetSyncEmailStatus){
        getFirestoreInstance();
        FirebaseUser user = getUser();
        if (user == null) {
            NullPointerException nullPointerException = new NullPointerException("Pengguna tidak ditemukan");
            Crashlytics.logException(nullPointerException);
            firestoreGetSyncEmailStatus.onCallback(new User(nullPointerException));
            return;
        }
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
    }

    public void changePassword(FirestoreGetChangePasswordStatus firestoreGetChangePasswordStatus, String oldPassword, String newPassword){
        getFirestoreInstance();
        FirebaseUser user = getUser();
        if (user == null) {
            NullPointerException nullPointerException = new NullPointerException("Pengguna tidak ditemukan");
            Crashlytics.logException(nullPointerException);
            firestoreGetChangePasswordStatus.onCallback(new User(nullPointerException));
            return;
        }
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
