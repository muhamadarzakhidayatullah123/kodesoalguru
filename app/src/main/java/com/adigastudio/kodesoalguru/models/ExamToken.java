package com.adigastudio.kodesoalguru.models;

public class ExamToken {
    private boolean isValid;
    private Throwable error;

    public ExamToken(boolean isValid) {
        this.isValid = isValid;
    }

    public ExamToken(Throwable error) {
        this.error = error;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
