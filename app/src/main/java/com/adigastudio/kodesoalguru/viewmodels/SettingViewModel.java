package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.repositories.AuthRepository;

import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {
    private OnClickListener callback;

    private String TAG = "SettingViewModel";

    public void doLogout() {
        new AuthRepository().signOut();
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
