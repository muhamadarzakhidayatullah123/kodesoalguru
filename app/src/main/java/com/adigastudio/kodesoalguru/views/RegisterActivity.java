package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.RegisterActivityBinding;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    private RegisterActivityBinding binding;
    private String TAG = "RegisterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.register_activity);
        ButterKnife.bind(this);

        binding.adView.loadAd(new AdConfig().getAdRequest());

        CheckConnectionData.Check(this, this, binding.registerActivity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }
}
