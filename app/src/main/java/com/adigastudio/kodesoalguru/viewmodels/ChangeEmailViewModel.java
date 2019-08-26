package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.User;
import com.adigastudio.kodesoalguru.repositories.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangeEmailViewModel extends ViewModel {

    private String TAG = "ChangeEmailViewModel";
    private MutableLiveData<User> data = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private OnClickListener callback;

    public void changeEmail(String oldEmail, String newEmail, String password){
        isLoading.setValue(true);
        new UserRepository().changeEmail(user -> {
            data.setValue(user);
            isLoading.postValue(false);
        }, oldEmail, newEmail, password);
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

    public LiveData<User> getChangeEmailStatus(){
        return data;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

}
