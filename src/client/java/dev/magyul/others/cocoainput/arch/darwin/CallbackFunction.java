package dev.magyul.others.cocoainput.arch.darwin;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import dev.magyul.others.cocoainput.CocoaInput;
import dev.magyul.others.cocoainput.util.Util;

public class CallbackFunction {
    //used to interact Objective-C code
    interface Func_insertText extends Callback {
        void invoke(String str, int position, int length);
    }

    interface Func_setMarkedText extends Callback {
        void invoke(String str, int position1, int length1, int position2, int length2);
    }

    interface Func_firstRectForCharacterRange extends Callback {
        Pointer invoke();
    }

    //used to provide Objective-C with logging way
    public static Callback Func_log = new Callback() {
        public void invoke(String msg) {
            CocoaInput.LOGGER.info(Util.getMarkerHeader(CocoaInput.OBJC) + msg);
        }
    };
    public static Callback Func_error = new Callback() {
        public void invoke(String msg) {
            CocoaInput.LOGGER.error(Util.getMarkerHeader(CocoaInput.OBJC) + msg);
        }
    };
    public static Callback Func_debug = new Callback() {
        public void invoke(String msg) {
            CocoaInput.LOGGER.debug(Util.getMarkerHeader(CocoaInput.OBJC) + msg);
        }
    };
}
