package com.adigastudio.kodesoalguru.models;

public class ForgotPassword {
    private boolean success;
    private Throwable error;

    public ForgotPassword(boolean success) {
        this.success = success;
    }

    public ForgotPassword(Throwable error) {
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
