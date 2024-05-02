package dev.magyul.others.cocoainput.plugin;

public interface IMEOperator {
    void setFocused(boolean inFocused);
    void discardMarkedText();
    void removeInstance();
}
