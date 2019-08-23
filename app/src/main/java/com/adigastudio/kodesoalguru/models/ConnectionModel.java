package com.adigastudio.kodesoalguru.models;

public class ConnectionModel {
    private int type;
    private Boolean isConnected;

    public ConnectionModel(int type, Boolean isConnected) {
        this.type = type;
        this.isConnected = isConnected;
    }

    public int getType() {
        return type;
    }

    public boolean getIsConnected() {
        return isConnected;
    }
}
