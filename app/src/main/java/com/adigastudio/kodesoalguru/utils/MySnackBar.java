package com.adigastudio.kodesoalguru.utils;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;
import static com.adigastudio.kodesoalguru.utils.MyEnum.NORMAL_MESSAGE;
import static com.adigastudio.kodesoalguru.utils.MyEnum.SUCCESS_MESSAGE;

public class MySnackBar {
    public static void Show(View view, String message, int length, int messageType){
        Snackbar snackbar = Snackbar.make(view, message, length);
        snackbar.getView().setBackgroundColor(Color.parseColor("#CC000000"));

        TextView tv = (TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);

        if (messageType == FAILED_MESSAGE) {
            tv.setTextColor(Color.parseColor("#F44336"));
        } else if (messageType == SUCCESS_MESSAGE) {
            tv.setTextColor(Color.parseColor("#29c665"));
        } else if (messageType == NORMAL_MESSAGE) {
            tv.setTextColor(Color.WHITE);
        }

        snackbar.setAction("TUTUP", v -> {
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
