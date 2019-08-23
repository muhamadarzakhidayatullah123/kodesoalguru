package com.adigastudio.kodesoalguru.viewmodels;

import android.view.View;


import com.adigastudio.kodesoalguru.interfaces.MyListeners;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;

import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private OnClickListener callback;

    private String TAG = "RegisterViewModel";

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
