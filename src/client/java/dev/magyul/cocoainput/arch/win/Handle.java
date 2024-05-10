package dev.magyul.cocoainput.arch.win;

import com.sun.jna.*;

public interface Handle extends Library {
    Handle INSTANCE = Native.load("libwincocoainput", Handle.class);

    void set_focus(int flag);

    void initialize(
            long window,
            PreeditCallback paramDrawCallback,
            DoneCallback paramDoneCallback,
            RectCallback paramRectCallback,
            Callback log,
            Callback error,
            Callback debug
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
