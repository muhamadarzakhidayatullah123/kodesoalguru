package com.adigastudio.kodesoalguru.views;

import android.os.Bundle;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.ErrorActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners;
import com.adigastudio.kodesoalguru.utils.ConnectionLiveData;
import com.adigastudio.kodesoalguru.viewmodels.ErrorViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class ErrorActivity extends AppCompatActivity {
    private ErrorActivityBinding binding;
    private ErrorViewModel viewModel;
    private MyListeners.OnClickListener clickListener;

    private boolean allowBack = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ErrorActivity.this, R.layout.error_activity);
        viewModel = ViewModelProviders.of(this).get(ErrorViewModel.class);
        binding.setViewModel(viewModel);

        clickListener = v -> {
            if (v.getId() == binding.buttonRetry.getId()) {
                onBackPressed();
            }
        };
        viewModel.setCallback(clickListener);

        String errorMessage = getIntent().getStringExtra("error_message");
        if (errorMessage != null) {
            binding.textError.setText(errorMessage);
        }

        ConnectionLiveData connectionLiveData = new ConnectionLiveData(this);
        connectionLiveData.observe(this, connection -> {
            if (connection.getIsConnected()) {

                allowBack = true;
            } else {
                allowBack = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (allowBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }
}
