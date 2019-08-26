package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.DetailAccountActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.viewmodels.DetailAccountViewModel;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class DetailAccountActivity extends AppCompatActivity {
    private DetailAccountActivityBinding binding;

    private DetailAccountViewModel viewModel;
    private OnClickListener clickListener;
    private String TAG = "DetailAccountActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(DetailAccountActivity.this, R.layout.detail_account_activity);

        checkUser();
        binding.adView.loadAd(new AdConfig().getAdRequest());

        viewModel = ViewModelProviders.of(this).get(DetailAccountViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.init();

        clickListener = v -> {
            if (v.getId() == binding.textChangeEmail.getId()) {
                Intent intent = new Intent(this, ChangeEmailActivity.class);
                startActivity(intent);
            } else if (v.getId() == binding.textChangePassword.getId()) {
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        };
        viewModel.setCallback(clickListener);

        viewModel.getIsLoading().observe(this, status -> {
            if(status){
                binding.scrollView.setVisibility(View.GONE);
                binding.shimmerViewContainer.startShimmer();
                binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            }
            else{
                binding.scrollView.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
            }
        });

        viewModel.getDetailAccount().observe(this, user -> {
            if (user.getError() == null) {
                binding.setUser(user);
            } else {
                Intent errorIntent = new Intent(getApplicationContext(), ErrorActivity.class);
                errorIntent.putExtra("error_message", MyErrorHandling.getErrorMessageFromThrowable(user.getError()));
                startActivity(errorIntent);
            }
        });

        CheckConnectionData.Check(getApplicationContext(), this, binding.getRoot());
    }

    private void checkUser(){
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                binding.textEmail.setText(user.getEmail());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
//        interstitialAd.setAdListener(null);
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
