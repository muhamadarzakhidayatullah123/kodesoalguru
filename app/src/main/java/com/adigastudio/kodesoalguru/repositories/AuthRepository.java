package com.adigastudio.kodesoalguru.repositories;

import com.adigastudio.kodesoalguru.models.ForgotPassword;
import com.adigastudio.kodesoalguru.models.Login;
import com.adigastudio.kodesoalguru.models.User;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {

    private FirebaseAuth firebaseAuth;
    private boolean isSuccess;
    private String message;
    private String TAG = "AuthRepository";

    private void getAuthInstance(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    public void signIn(FirebaseAuthGetLoginCallback firebaseAuthGetLoginCallback, Login user){
        getAuthInstance();
        firebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getUser().getIdToken(false).addOnSuccessListener(result -> {
                    boolean isStudent = (boolean) result.getClaims().get("student");
                    //false = teacher, moderator, admin
                    if (!isStudent) {
                        isSuccess = true;
                        message = "Berhasil masuk";
                    } else {
                        isSuccess = false;
                        message = MyErrorHandling.getErrorMessageFromString("ERROR_INVALID_CUSTOM_CLAIM");
                    }
                    firebaseAuthGetLoginCallback.onCallback(new Login(isSuccess, message));
                });
            } else {
                isSuccess = false;
                try {
                    message = MyErrorHandling.getErrorMessageFromString(((FirebaseAuthException) task.getException()).getErrorCode());
                } catch (Exception err){
                    message = "Terjadi kesalahan koneksi";
                }
                firebaseAuthGetLoginCallback.onCallback(new Login(isSuccess, message));
            }
        });
    }

    public void sendEmail(FirebaseAuthSendEmailCallback firebaseAuthSendEmailCallback){
        getAuthInstance();
        FirebaseUser user = getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseAuthSendEmailCallback.onCallback(new User(user.getEmail()));
            } else {
                firebaseAuthSendEmailCallback.onCallback(new User(task.getException()));
            }
        });
    }

    public void forgotPassword(FirebaseAuthForgotPasswordCallback firebaseAuthForgotPasswordCallback, String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseAuthForgotPasswordCallback.onCallback(new ForgotPassword(true));
            } else {
                firebaseAuthForgotPasswordCallback.onCallback(new ForgotPassword(task.getException()));
            }
        });
    }

    public void reload(FirebaseAuthReloadCallback firebaseAuthReloadCallback){
        getAuthInstance();
        FirebaseUser user = getCurrentUser();
        user.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseAuthReloadCallback.onCallback(new User(user.isEmailVerified()));
            } else {
                firebaseAuthReloadCallback.onCallback(new User(task.getException()));
            }
        });
    }

    public void signOut(){
        getAuthInstance();
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser(){
        getAuthInstance();
        return firebaseAuth.getCurrentUser();
    }

    public interface FirebaseAuthGetLoginCallback {
        void onCallback(Login loginUser);
    }

    public interface FirebaseAuthSendEmailCallback {
        void onCallback(User user);
    }

    public interface FirebaseAuthForgotPasswordCallback {
        void onCallback(ForgotPassword forgotPassword);
    }

    public interface FirebaseAuthReloadCallback {
        void onCallback(User user);
    }
}
