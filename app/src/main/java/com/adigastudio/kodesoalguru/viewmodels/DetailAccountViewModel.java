package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.User;
import com.adigastudio.kodesoalguru.repositories.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailAccountViewModel extends ViewModel {

    private String TAG = "DetailAccountViewModel";
    private MutableLiveData<User> data = new MutableLiveData<>();
    private MutableLiveData<User> dataSyncEmail = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSyncEmailLoading = new MutableLiveData<>();
    private OnClickListener callback;

    public void init(){
        isLoading.setValue(true);
        new UserRepository().getUserDetail(user -> {
            data.setValue(user);
            isLoading.postValue(false);
        });
    }

    public void syncEmail(){
        isSyncEmailLoading.setValue(true);
        new UserRepository().syncEmail(user -> {
            dataSyncEmail.setValue(user);
            isSyncEmailLoading.postValue(false);
        });
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

    public LiveData<User> getDetailAccount(){
        return data;
    }

    public LiveData<User> getSyncEmail(){
        return dataSyncEmail;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public LiveData<Boolean> getIsSyncEmailLoading(){
        return isSyncEmailLoading;
    }

}
