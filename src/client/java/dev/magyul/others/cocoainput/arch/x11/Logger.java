package dev.magyul.others.cocoainput.arch.x11;

import com.sun.jna.Callback;
import dev.magyul.others.cocoainput.CocoaInput;
import dev.magyul.others.cocoainput.util.Util;

public class Logger {

    public static void log(String msg, Object... data) {
        CocoaInput.LOGGER.info(Util.getMarkerHeader(CocoaInput.JAVA) + msg, data);
    }

    public static void error(String msg, Object... data) {
        CocoaInput.LOGGER.error(Util.getMarkerHeader(CocoaInput.JAVA) + msg, data);
    }

    public static void debug(String msg, Object... data) {
        CocoaInput.LOGGER.debug(Util.getMarkerHeader(CocoaInput.JAVA) + msg, data);
    }

    public static Callback clangLog = new Callback() {
        public void invoke(String msg) {
            CocoaInput.LOGGER.info(Util.getMarkerHeader(CocoaInput.CLANG) + msg);
        }
    };
    public static Callback clangError = new Callback() {
        public void invoke(String msg) {
            CocoaInput.LOGGER.error(Util.getMarkerHeader(CocoaInput.CLANG) + msg);
        }
    };
    public static Callback clangDebug = new Callback() {
        public void invoke(String msg) {
            CocoaInput.LOGGER.debug(Util.getMarkerHeader(CocoaInput.CLANG) + msg);
        }
    };
}
