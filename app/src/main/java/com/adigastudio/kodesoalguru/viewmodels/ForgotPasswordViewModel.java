package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.ForgotPassword;
import com.adigastudio.kodesoalguru.repositories.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForgotPasswordViewModel extends ViewModel {
    private MutableLiveData<ForgotPassword> forgotPassword;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();
    private OnClickListener callback;

    private String TAG = "LoginViewModel";

    public MutableLiveData<ForgotPassword> getForgotPassword() {
        if (forgotPassword == null) {
            forgotPassword = new MutableLiveData<>();
        }
        return forgotPassword;
    }

    public void forgotPassword(final String email) {
        isLoading.setValue(true);
        new AuthRepository().forgotPassword(isSuccess -> {
            forgotPassword.setValue(isSuccess);
            isLoading.postValue(false);
        }, email);
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public void onClicked(OnClickListener callback, View view){
        callback.onClicked(view);
    }

    public void setCallback(OnClickListener callback){
        this.callback = callback;
    }

    public OnClickListener getCallback() {
        return this.callback;
    }
}
