package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.ChangePasswordActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.Login;
import com.adigastudio.kodesoalguru.models.User;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.MyErrorHandling;
import com.adigastudio.kodesoalguru.utils.MyProgressBar;
import com.adigastudio.kodesoalguru.utils.MySnackBar;
import com.adigastudio.kodesoalguru.viewmodels.ChangePasswordViewModel;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;

public class ChangePasswordActivity extends AppCompatActivity {
    private ChangePasswordActivityBinding binding;

    private ChangePasswordViewModel viewModel;
    private OnClickListener clickListener;
    private String TAG = "ChangePasswordActivity";
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ChangePasswordActivity.this, R.layout.change_password_activity);
        MyProgressBar.init(this, binding.appBar.progressBar, android.R.color.white);

        checkUser();
        binding.adView.loadAd(new AdConfig().getAdRequest());

        viewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        binding.setViewModel(viewModel);

        clickListener = v -> {
            loadInterstitialAd();
            if (v.getId() == binding.buttonSave.getId()) {
                String oldPassword = binding.editOldPassword.getText().toString();
                String newPassword = binding.editNewPassword.getText().toString();
                String confirmPassword = binding.editConfirmPassword.getText().toString();

                if (!newPassword.equals(confirmPassword)) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_password_not_match), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                    return;
                }

                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_empty_password), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else if (!new Login(oldPassword).isPasswordLengthGreaterThan6() || !new Login(newPassword).isPasswordLengthGreaterThan6() || !new Login(confirmPassword).isPasswordLengthGreaterThan6()) {
                    MySnackBar.Show(binding.getRoot(), getString(R.string.error_minimum_password_character), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                }
                else {
                    viewModel.changePassword(oldPassword, newPassword);
                }
            } else if (v.getId() == binding.imageShowHideOldPassword.getId()) {
                onPasswordHintClick(binding.editOldPassword);
            } else if (v.getId() == binding.imageShowHideNewPassword.getId()) {
                onPasswordHintClick(binding.editNewPassword);
            } else if (v.getId() == binding.imageShowHideConfirmPassword.getId()) {
                onPasswordHintClick(binding.editConfirmPassword);
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

        viewModel.getChangePasswordStatus().observe(this, user -> {
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
        });

        CheckConnectionData.Check(getApplicationContext(), this, binding.getRoot());
    }

    private void getStatus(User user){
        if (user.getError() == null) {
            new FancyGifDialog.Builder(ChangePasswordActivity.this)
                    .setTitle(getString(R.string.success))
                    .setMessage(getString(R.string.success_change_password))
                    .setPositiveBtnText(getString(R.string.back))
                    .setGifResource(R.drawable.img_success)
                    .isCancellable(false)
                    .OnPositiveClicked(() -> {
                        onBackPressed();
                    })
                    .build();
        } else {
            new FancyGifDialog.Builder(ChangePasswordActivity.this)
                    .setTitle(getString(R.string.error_change_password))
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

    private void loadInterstitialAd(){
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getString(R.string.ad_unit_interstitial));
        }
        interstitialAd.loadAd(new AdConfig().getAdRequest());
    }

    public void onPasswordHintClick(EditText editText) {
        if (editText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private void checkUser(){
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        if (interstitialAd != null) {
            interstitialAd.setAdListener(null);
        }
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
