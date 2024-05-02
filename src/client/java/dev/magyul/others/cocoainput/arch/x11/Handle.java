package dev.magyul.others.cocoainput.arch.x11;

import com.sun.jna.*;

public interface Handle extends Library {
    Handle INSTANCE = Native.load("x11cocoainput", Handle.class);


    void set_focus(int flag);

    void initialize(
            long window,
            long xw,
            DrawCallback paramDrawCallback,
            DoneCallback paramDoneCallback,
            Callback log,
            Callback error,
            Callback debug
    );

    public static interface DrawCallback extends Callback {
        Pointer invoke(int param1Int1, int param1Int2, int param1Int3, short param1Short, boolean param1Boolean, String param1String, WString param1WString, int param1Int4, int param1Int5, int param1Int6);
    }

    public static interface DoneCallback extends Callback {
        void invoke();
    }
}
