package com.adigastudio.kodesoalguru.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.adigastudio.kodesoalguru.MainApplication;
import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.databinding.LoginActivityBinding;
import com.adigastudio.kodesoalguru.interfaces.MyListeners;
import com.adigastudio.kodesoalguru.interfaces.MyListeners.OnClickListener;
import com.adigastudio.kodesoalguru.models.Login;
import com.adigastudio.kodesoalguru.utils.AdConfig;
import com.adigastudio.kodesoalguru.utils.CheckConnectionData;
import com.adigastudio.kodesoalguru.utils.MyProgressBar;
import com.adigastudio.kodesoalguru.utils.MySnackBar;
import com.adigastudio.kodesoalguru.viewmodels.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import pub.devrel.easypermissions.EasyPermissions;

import static com.adigastudio.kodesoalguru.utils.MyEnum.FAILED_MESSAGE;
import static com.adigastudio.kodesoalguru.utils.MyEnum.SUCCESS_MESSAGE;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";
    private LoginViewModel viewModel;
    private LoginActivityBinding binding;
    private OnClickListener clickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.login_activity);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        MyProgressBar.init(this, binding.progressBar, android.R.color.white);

        clickListener = v -> {
            if (v.getId() == binding.imageShowHidePassword.getId()) {
                onPasswordHintClick();
            } else if (v.getId() == binding.buttonLogin.getId()) {
                onSignInClick();
            } else if (v.getId() == binding.buttonRegister.getId()) {
                onSignUpClick();
            } else if (v.getId() == binding.textForgotPassword.getId()) {
                onForgotPasswordClick();
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

        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } else {
                binding.adView.loadAd(new AdConfig().getAdRequest());
            }
        });

        viewModel.getUser().observe(this, loginUser -> {
            if (loginUser.isSuccess()) {
                Log.d(TAG, loginUser.getMessage());
                MySnackBar.Show(binding.loginActivity, loginUser.getMessage(), Snackbar.LENGTH_LONG, SUCCESS_MESSAGE);
            } else {
                Log.d(TAG, loginUser.getMessage());
                MySnackBar.Show(binding.loginActivity, loginUser.getMessage(), Snackbar.LENGTH_LONG, FAILED_MESSAGE);
                viewModel.doLogout();
            }
        });

        CheckConnectionData.Check(this, this, binding.loginActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.adView.setAdListener(null);
        binding.adView.destroy();
        MainApplication.getRefWatcher(getApplicationContext()).watch(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void showProgressBar(){
        binding.progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar(){
        binding.progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void onPasswordHintClick() {
        if (binding.editPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            binding.editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            binding.editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void onSignInClick() {
        Login loginUser = new Login(binding.editEmail.getText().toString(), binding.editPassword.getText().toString());
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
        else {
            viewModel.doLogin(loginUser);
        }
    }

    public void onSignUpClick() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordClick() {
        Intent intent = new Intent(this, ForgotPassowrdActivity.class);
        startActivity(intent);
    }
}