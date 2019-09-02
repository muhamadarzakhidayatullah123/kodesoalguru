package com.adigastudio.kodesoalguru.models;

public class AppUpdate {
    private long versionCode;
    private boolean forceUpdate;

    public AppUpdate(long versionCode, boolean forceUpdate) {
        this.versionCode = versionCode;
        this.forceUpdate = forceUpdate;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
