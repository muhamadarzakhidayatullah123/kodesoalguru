package com.adigastudio.kodesoalguru.utils;

import android.util.Log;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctionsException;

public class MyErrorHandling {
    public static String getErrorMessageFromString(String errorCode){
        switch (errorCode){
            //Firebase Authentication
            case "ERROR_USER_NOT_FOUND":
                return MainApplication.getResource().getString(R.string.error_user_not_found);
            case "ERROR_WRONG_PASSWORD":
                return MainApplication.getResource().getString(R.string.error_wrong_password);
            case "NO_CONNECTION":
                return MainApplication.getResource().getString(R.string.error_no_connetion);
            case "ERROR_INVALID_CUSTOM_CLAIM":
                return MainApplication.getResource().getString(R.string.error_invalid_custom_claim);
            case "DEADLINE_EXCEEDED":
                return MainApplication.getResource().getString(R.string.error_deadline_exceeded);
            case "ERROR_INVALID_EMAIL":
                return MainApplication.getResource().getString(R.string.error_invalid_email);
            case "ERROR_EMAIL_ALREADY_IN_USE":
                return MainApplication.getResource().getString(R.string.error_email_already_in_use);
            case "ERROR_USER_TOKEN_EXPIRED":
                return MainApplication.getResource().getString(R.string.error_user_token_expired);
            case "NO_DATA":
                return MainApplication.getResource().getString(R.string.no_data);
            case "INTERNAL":
                return MainApplication.getResource().getString(R.string.error_system);
            default:
                return errorCode;
        }
    }

    public static String getErrorMessageFromThrowable(Throwable throwable){
        if (throwable instanceof FirebaseFunctionsException) {
            FirebaseFunctionsException exception = (FirebaseFunctionsException) throwable;
            return getErrorMessageFromString(exception.getCode().toString());
        } else if (throwable instanceof FirebaseAuthException) {
            FirebaseAuthException exception = (FirebaseAuthException) throwable;
            return getErrorMessageFromString(exception.getErrorCode());
        } else if (throwable instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException exception = (FirebaseFirestoreException) throwable;
            return getErrorMessageFromString(exception.getCode().toString());
        } else {
            Crashlytics.logException(throwable);
            Log.d("CRASH", throwable.getMessage());
            return MainApplication.getResource().getString(R.string.error_system);
        }
    }
}
