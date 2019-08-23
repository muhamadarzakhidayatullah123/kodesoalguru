package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.Login;
import com.adigastudio.kodesoalguru.repositories.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Login> userData;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private OnClickListener callback;

    private String TAG = "LoginViewModel";

    public MutableLiveData<Login> getUser() {
        if (userData == null) {
            userData = new MutableLiveData<>();
        }
        return userData;
    }

    public void doLogin(final Login user) {
        isLoading.setValue(true);
        new AuthRepository().signIn(loginUser -> {
            userData.setValue(loginUser);
            firebaseUser.setValue(new AuthRepository().getCurrentUser());
            isLoading.postValue(false);
        }, user);
    }

    public void doLogout() {
        new AuthRepository().signOut();
        firebaseUser.setValue(null);
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        firebaseUser.setValue(new AuthRepository().getCurrentUser());
        return firebaseUser;
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
