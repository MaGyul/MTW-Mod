package dev.magyul.cocoainput.arch.darwin;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import dev.magyul.cocoainput.CocoaInput;
import dev.magyul.cocoainput.util.Util;

import static dev.magyul.cocoainput.util.Logger.LogFunction;

public class CallbackFunction {
    //used to interact Objective-C code
    public interface Func_insertText extends Callback {
        void invoke(String str, int position, int length);
    }

    public interface Func_setMarkedText extends Callback {
        void invoke(String str, int position1, int length1, int position2, int length2);
    }

    public interface Func_firstRectForCharacterRange extends Callback {
        Pointer invoke();
    }

    //used to provide Objective-C with logging way
    public static LogFunction Func_log = msg -> CocoaInput.LOGGER.info("{}{}", Util.getMarkerHeader(CocoaInput.OBJC), msg);
    public static LogFunction Func_error = msg -> CocoaInput.LOGGER.error("{}{}", Util.getMarkerHeader(CocoaInput.OBJC), msg);
    public static LogFunction Func_debug = msg -> CocoaInput.LOGGER.debug("{}{}", Util.getMarkerHeader(CocoaInput.OBJC), msg);
}
