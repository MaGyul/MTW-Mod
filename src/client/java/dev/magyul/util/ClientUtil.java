package dev.magyul.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClientUtil {
    private static final UUID[] devs = new UUID[]{
            UUID.fromString("98604430-65db-499b-be91-a9d6a2602304"),    // Boo
            UUID.fromString("feb58aa8-56f6-4728-8546-82071e39dd24"),    // Main
//            UUID.fromString("7a0f0c5a-6cb7-4c31-9cd7-1ff6ecf637e1"),    // Suhok
    };
    private static final UUID test = UUID.fromString("825203c1-484b-4935-bea2-3f7aa11e142a");
    private static final String serverDevURL = "https://mathwor.com/client/developer.list";
    private static final boolean local = false;
    public static final ServerInfo mtw_info = new ServerInfo("MTW Server", local ? "localhost" : "play.mathwor.com", ServerInfo.ServerType.OTHER) {
        {
            setResourcePackPolicy(ResourcePackPolicy.ENABLED);
        }
    };
    public static final ServerAddress mtw_address = new ServerAddress(mtw_info.address, 25565);
    public static final SystemToast.Type MTW_TOAST = new SystemToast.Type();
    public static final Map<UUID, String> receivedAllMic = new HashMap<>();
    public static int pickupReach = 45;
    public static ConnectServer cs;
    public static Text playStatus;
    private static boolean allMic = false;

    public static boolean checkDev() {
        var session = MinecraftClient.getInstance().getSession();
        if (Objects.equals(session.getAccessToken(), "FabricMC")) return true;
        var uuid = session.getUuidOrNull();
        for (var dev : devs) {
            if (dev.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkServerDev() {
        var session = MinecraftClient.getInstance().getSession();
        if (Objects.equals(session.getAccessToken(), "FabricMC")) return true;
        var uuid = session.getUuidOrNull();
        return getServerDev().contains(uuid);
    }

    public static boolean checkTest() {
        var session = MinecraftClient.getInstance().getSession();
        return test.equals(session.getUuidOrNull());
    }

    public static boolean isAllMic() {
        return allMic;
    }

    public static void setAllMic(boolean value) {
        allMic = value;
    }

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {}
    }

    public static Timer setTimeout(Runnable run, long delay) {
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MinecraftClient.getInstance().execute(run);
            }
        }, delay);
        return timer;
    }

    public static Timer setInterval(Runnable run, long period) {
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MinecraftClient.getInstance().execute(run);
            }
        }, period, period);
        return timer;
    }

    public static Timer setInterval(Runnable run, long delay, long period) {
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MinecraftClient.getInstance().execute(run);
            }
        }, delay, period);
        return timer;
    }

    public static void cancelTimer(Timer timer) {
        timer.cancel();
    }

    public static boolean isKeyDown(int key) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key);
    }

    public static boolean isMouseDown(int key) {
        return GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), key) == 1;
    }

    public static Text getKeyName(int key) {
        return InputUtil.fromKeyCode(key, -1).getLocalizedText();
    }

    public static Text getMouseName(int mouse) {
        return InputUtil.Type.MOUSE.createFromCode(mouse).getLocalizedText();
    }

    private static List<UUID> getServerDev() {
        try (InputStream is = new URI(serverDevURL).toURL().openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return rd.lines().filter(s -> !s.startsWith("//")).map(UUID::fromString).toList();
        } catch (Exception e) {
            var list = new ArrayList<>(Arrays.asList(devs));
            list.add(UUID.fromString("7a0f0c5a-6cb7-4c31-9cd7-1ff6ecf637e1")); // Suhok
            return list;
        }
    }
}
