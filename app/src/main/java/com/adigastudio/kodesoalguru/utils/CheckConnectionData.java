package com.adigastudio.kodesoalguru.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.views.ErrorActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.lifecycle.LifecycleOwner;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;
import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;

public class CheckConnectionData {
    private static String TAG = "CheckConnectionData";
    public static void Check(Context context, LifecycleOwner owner, View view){
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(context);
        connectionLiveData.observe(owner, connection -> {
            if (connection.getIsConnected()) {
                Log.d(TAG, "Check: connected");
//                MySnackBar.checkToken(view, "Online", Snackbar.LENGTH_SHORT, SUCCESS_MESSAGE);
            } else {
                Log.d(TAG, "Check: disconnected");
                MySnackBar.Show(view, context.getResources().getString(R.string.error_no_connetion), Snackbar.LENGTH_INDEFINITE, FAILED_MESSAGE);
                Intent intent = new Intent(context, ErrorActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
