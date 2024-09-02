package dev.magyul.cocoainput.arch.win;

import com.sun.jna.*;

import static dev.magyul.cocoainput.util.Logger.LogFunction;

public interface Handle extends Library {
    Handle INSTANCE = Native.load("libwincocoainput", Handle.class);

    void set_focus(int flag);

    void initialize(
            long window,
            PreeditCallback paramDrawCallback,
            DoneCallback paramDoneCallback,
            RectCallback paramRectCallback,
            LogFunction log,
            LogFunction error,
            LogFunction debug
    );

    interface PreeditCallback extends Callback {
        void invoke(WString str, int cursor, int length);
    }

    interface DoneCallback extends Callback {
        void invoke(WString str);
    }

    interface RectCallback extends Callback {
        int invoke(Pointer p);
    }
}
