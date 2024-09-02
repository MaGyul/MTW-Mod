package dev.magyul.cocoainput.util;

import com.sun.jna.Callback;
import dev.magyul.cocoainput.CocoaInput;
import org.apache.logging.log4j.message.ParameterizedMessage;

public class Logger {

    public static void log(String msg, Object... data) {
        CocoaInput.LOGGER.info("{}{}", Util.getMarkerHeader(CocoaInput.JAVA), ParameterizedMessage.format(msg, data));
    }

    public static void error(String msg, Object... data) {
        CocoaInput.LOGGER.error("{}{}", Util.getMarkerHeader(CocoaInput.JAVA), ParameterizedMessage.format(msg, data));
    }

    public static void debug(String msg, Object... data) {
        CocoaInput.LOGGER.debug("{}{}", Util.getMarkerHeader(CocoaInput.JAVA), ParameterizedMessage.format(msg, data));
    }

    public static LogFunction clangLog = msg -> CocoaInput.LOGGER.info("{}{}", Util.getMarkerHeader(CocoaInput.CLANG), msg);
    public static LogFunction clangError = msg -> CocoaInput.LOGGER.error("{}{}", Util.getMarkerHeader(CocoaInput.CLANG), msg);
    public static LogFunction clangDebug = msg -> CocoaInput.LOGGER.debug("{}{}", Util.getMarkerHeader(CocoaInput.CLANG), msg);

    public interface LogFunction extends Callback {
        void invoke(String msg);
    }
}
