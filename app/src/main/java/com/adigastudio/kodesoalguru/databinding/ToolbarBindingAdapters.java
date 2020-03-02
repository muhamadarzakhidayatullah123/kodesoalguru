package com.adigastudio.kodesoalguru.databinding;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.BindingAdapter;

public class ToolbarBindingAdapters {
    private static String TAG = "ToolbarBindingAdapters";

    @BindingAdapter("onNavigationClick")
    public static void onNavigationClicked(Toolbar toolbar, String param){
        toolbar.setNavigationOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) toolbar.getContext();
            activity.onBackPressed();
        });
    }

    @BindingAdapter("onExamNavigationClick")
    public static void onExamNavigationClicked(ImageButton toolbar, String param){
        toolbar.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) toolbar.getContext();
            activity.onBackPressed();
        });
    }
}
