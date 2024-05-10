package dev.magyul.cocoainput;

import com.sun.jna.Platform;
import dev.magyul.cocoainput.arch.win.WinController;
import dev.magyul.cocoainput.plugin.CocoaInputController;
import dev.magyul.cocoainput.arch.darwin.DarwinController;
import dev.magyul.cocoainput.arch.dummy.DummyController;
import dev.magyul.cocoainput.arch.x11.X11Controller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CocoaInput {
    public static Logger LOGGER = LoggerFactory.getLogger("CocoaInput");
    public static Marker OBJC = MarkerFactory.getMarker("ObjC");
    public static Marker JAVA = MarkerFactory.getMarker("Java");
    public static Marker CLANG = MarkerFactory.getMarker("Clang");

    private static CocoaInputController controller;
    private static String zipSource;
    public static boolean initialized = false;

    public static void init(String zipFile) {
        CocoaInput.zipSource = zipFile;
        try {
            if (Platform.isMac()) {
                CocoaInput.applyController(new DarwinController());
            } else if (Platform.isWindows()) {
                CocoaInput.applyController(new WinController());
            } else if (Platform.isX11()) {
                CocoaInput.applyController(new X11Controller());
            } else {
                LOGGER.warn("CocoaInput cannot find appropriate Controller in running OS.");
                CocoaInput.applyController(new DummyController());
            }
            LOGGER.info("CocoaInput has been initialized.");
            initialized = true;
        } catch (Exception ex) {
            LOGGER.error("CocoaInput init error: ", ex);
        }
    }

    public static double getScreenScaledFactor() {
        return MinecraftClient.getInstance().getWindow().getScaleFactor();
    }

    public static void applyController(CocoaInputController controller) {
        CocoaInput.controller = controller;
        LOGGER.info("CocoaInput is now using controller: {}", controller.getClass());
    }

    public static CocoaInputController getController() {
        return controller;
    }

    public static void distributeScreen(Screen screen) {
        if (controller != null) {
            controller.screenOpenNotify(screen);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyLibrary(String libraryName, String libraryPath) throws IOException {
        InputStream libFile;
        if (zipSource == null) {
            libFile = CocoaInput.class.getResourceAsStream("/" + libraryPath);
        } else {
            try (ZipFile jarFile = new ZipFile(CocoaInput.zipSource)) {
                libFile = jarFile.getInputStream(new ZipEntry(libraryPath));
            } catch (FileNotFoundException ex) {
                LOGGER.warn("Couldn't get library path. Is this debug mode?");
                libFile = ClassLoader.getSystemResourceAsStream(libraryPath);
            }
        }
        if (libFile == null) {
            LOGGER.error("Library path is null.");
            return;
        }
        var client = MinecraftClient.getInstance();
        File nativeDir = new File(client.runDirectory.getAbsolutePath().concat("/native"));
        File copyLibFile = new File(nativeDir, libraryName);
        if (!nativeDir.exists()) {
            nativeDir.mkdir();
        }
        if (!copyLibFile.exists()) {
            try (FileOutputStream fos = new FileOutputStream(copyLibFile)) {
                copyLibFile.createNewFile();
                IOUtils.copy(libFile, fos);
            } catch (IOException ex) {
                LOGGER.error("Attempted to copy library to ./native/{} but failed.", libraryName);
                throw ex;
            }
            System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
            LOGGER.info("CocoaInput has copied library to native directory.");
        } else {
            System.setProperty("jna.library.path", nativeDir.getAbsolutePath());
        }
    }
}
