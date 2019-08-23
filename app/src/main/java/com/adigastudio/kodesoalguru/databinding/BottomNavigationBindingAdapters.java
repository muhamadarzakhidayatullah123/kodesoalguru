package com.adigastudio.kodesoalguru.databinding;

import android.view.MenuItem;

import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnNavigationItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

public class BottomNavigationBindingAdapters {
    private static String TAG = "BottomNavigationBindingAdapters";

    @BindingAdapter("onBottomNavigationClick")
    public static void onBottomNavigationClicked(BottomNavigationView view, OnNavigationItemSelectedListener listener){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                listener.OnNavigationItemSelected(menuItem);
                return true;
            }
        });
    }
}
