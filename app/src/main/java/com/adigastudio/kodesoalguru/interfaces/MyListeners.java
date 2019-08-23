package com.adigastudio.kodesoalguru.interfaces;

import android.view.MenuItem;
import android.view.View;

public class MyListeners {
    public interface OnClickListener {
        void onClicked(View v);
    }

    public interface OnNavigationItemSelectedListener {
        void OnNavigationItemSelected(MenuItem menuItem);
    }

    public interface OnItemClickedListener<T> {
        void onClicked(T item);
    }

    public interface OnItemListClickedListener<T> {
        void onClicked(View v, T item);
    }

    public interface OnHandlerListener<T> {
        void onTick(T arg);
    }

    public interface OnItemWithPositionClickedListener<T> {
        void onClicked(T item, int position);
    }
}
