package dev.magyul.api;

public enum LogicalSide {
    CLIENT,
    SERVER;

    LogicalSide() {
    }

    public boolean isServer() {
        return !this.isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}
