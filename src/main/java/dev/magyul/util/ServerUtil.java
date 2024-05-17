package dev.magyul.util;

import dev.magyul.registers.MTWItems;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerUtil {
    public static final DoubleKeyMap<UUID, Integer, Boolean> playerKey = new DoubleKeyMap<>();
    public static AtomicBoolean allowLogins = new AtomicBoolean(false);

    public static boolean isKeyDown(@NotNull PlayerEntity player, int key) {
        var map = playerKey.get(player.getUuid());
        if (map == null) return false;
        if (!map.containsKey(key)) return false;
        return map.get(key);
    }

    public static boolean isRegionRootMod(@NotNull PlayerEntity player) {
        var mainHand = player.getMainHandStack();
        return isKeyDown(player, GLFW.GLFW_KEY_LEFT_CONTROL) &&
                player.isCreative() &&
                player.hasPermissionLevel(2) &&
                mainHand.isOf(MTWItems.MTW_REGION_VIEWER);
    }

    public static void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }
    }

    public static Timer setTimeout(Runnable run, long delay) {
        var timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, delay);
        return timer;
    }

    public static Timer setInterval(Runnable run, long period) {
        var timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, period, period);
        return timer;
    }

    public static Timer setInterval(Runnable run, long delay, long period) {
        var timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                run.run();
            }
        }, delay, period);
        return timer;
    }

    public static void cancelTimer(Timer timer) {
        if (timer != null) timer.cancel();
    }
}
