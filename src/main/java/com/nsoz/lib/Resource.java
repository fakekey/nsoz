package com.nsoz.lib;

public class Resource {

    private long timeRemoveResource;
    private byte[] data;
    private long createdAt;

    public Resource(byte[] data, long timeRemoveResource) {
        this.data = data;
        this.timeRemoveResource = timeRemoveResource;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        boolean isExpired = (System.currentTimeMillis() - createdAt) > timeRemoveResource;
        return isExpired;
    }

    public byte[] getData() {
        return this.data;
    }
}
