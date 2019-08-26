package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.ChangeEmailActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.Login;
import com.adigastudio.kodesoalguru.models.User;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.utils.MyProgressBar;
import com.adigastudio.kodesoalguru.utils.MySnackBar;
import com.adigastudio.kodesoalguru.viewmodels.ChangeEmailViewModel;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.adigastudio.kodesoalguru.viewmodels.SettingViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;

public class ChangeEmailActivity extends AppCompatActivity {
    private ChangeEmailActivityBinding binding;

    private ChangeEmailViewModel viewModel;
    private OnClickListener clickListener;
    private String TAG = "ChangeEmailActivity";
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ChangeEmailActivity.this, R.layout.change_email_activity);
        MyProgressBar.init(getApplicationContext(), binding.appBar.progressBar, android.R.color.white);

        checkUser();
        binding.adView.loadAd(new AdConfig().getAdRequest());
        loadInterstitialAd();

        viewModel = ViewModelProviders.of(this).get(ChangeEmailViewModel.class);
        binding.setViewModel(viewModel);

        clickListener = v -> {
            if (v.getId() == binding.buttonSave.getId()) {
                Login loginUser = new Login(binding.editNewEmail.getText().toString(), binding.editPassword.getText().toString());
                if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getEmail())) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_empty_email), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else if (!loginUser.isEmailValid()) {
                    MySnackBar.Show(binding.getRoot(),getString(R.string.error_invalid_email), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getPassword())) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_empty_password), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else if (!loginUser.isPasswordLengthGreaterThan6()) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_minimum_password_character), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else if (binding.editOldEmail.getText().toString().equals(binding.editNewEmail.getText().toString())) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_old_email_equals_new_email), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else {
                    viewModel.changeEmail(binding.editOldEmail.getText().toString(), binding.editNewEmail.getText().toString(), binding.editPassword.getText().toString());
                }
            } else if (v.getId() == binding.imageShowHidePassword.getId()) {
                onPasswordHintClick();
            }
        };
        viewModel.setCallback(clickListener);

        viewModel.getIsLoading().observe(this, status -> {
            if (status) {
                showProgressBar();
            } else {
                hideProgressBar();
            }
        });

        viewModel.getChangeEmailStatus().observe(this, user -> {
            if (user.getError() == null) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            getStatus(user);
                        }
                    });
                } else {
                    getStatus(user);
                }
            } else {
                Intent errorIntent = new Intent(getApplicationContext(), ErrorActivity.class);
                errorIntent.putExtra("error_message", MyErrorHandling.getErrorMessageFromThrowable(user.getError()));
                startActivity(errorIntent);
            }
        });

        CheckConnectionData.Check(getApplicationContext(), this, binding.getRoot());
    }

    private void loadInterstitialAd(){
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getString(R.string.ad_unit_interstitial));
        }
        interstitialAd.loadAd(new AdConfig().getAdRequest());
    }

    private void getStatus(User user) {
        if (user.getError() == null) {
            new FancyGifDialog.Builder(ChangeEmailActivity.this)
                    .setTitle(getString(R.string.success))
                    .setMessage(String.format(getString(R.string.success_change_email), binding.editNewEmail.getText().toString()))
                    .setPositiveBtnText(getString(R.string.back))
                    .setGifResource(R.drawable.img_success)
                    .isCancellable(false)
                    .OnPositiveClicked(() -> {
                        SettingViewModel viewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
                        viewModel.doLogout();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    })
                    .build();
        } else {
            new FancyGifDialog.Builder(ChangeEmailActivity.this)
                    .setTitle(getString(R.string.error_change_email))
                    .setMessage(MyErrorHandling.getErrorMessageFromThrowable(user.getError()))
                    .setPositiveBtnText(getString(R.string.back))
                    .setGifResource(R.drawable.img_danger)
                    .isCancellable(false)
                    .OnPositiveClicked(() -> {
                        loadInterstitialAd();
                    })
                    .build();
        }
    }

    public void onPasswordHintClick() {
        if (binding.editPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            binding.editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            binding.editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void checkUser(){
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                binding.editOldEmail.setText(user.getEmail());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        interstitialAd.setAdListener(null);
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideProgressBar();
    }

    private void showProgressBar(){
        binding.appBar.progressBar.setVisibility(View.VISIBLE);
        binding.appBar.toolbar.setNavigationIcon(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar(){
        binding.appBar.progressBar.setVisibility(View.GONE);
        binding.appBar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_32dp));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}