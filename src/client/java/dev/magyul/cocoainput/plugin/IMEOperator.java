package dev.magyul.cocoainput.plugin;

public interface IMEOperator {
    void setFocused(boolean inFocused);
    void discardMarkedText();
    void removeInstance();
}
