package dev.magyul.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

public class SuggestionUtil {

    @Unique
    @Nullable
    public static PlayerListEntry getPlayerListEntry(String text) {
        if (text.startsWith("@") ||
                text.contains("[") ||
                text.contains("]") ||
                isPosition(text) || isBlockPos(text) || isInt(text)) {
            return null;
        }
        var client = MinecraftClient.getInstance();
        var networkHandler = client.getNetworkHandler();
        if (networkHandler != null) {
            if (isUUID(text)) {
                return networkHandler.getPlayerListEntry(UUID.fromString(text));
            }
            return networkHandler.getPlayerListEntry(text);
        }
        return null;
    }

    @Unique
    private static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Unique
    private static boolean isPosition(String string) {
        return string.matches("^-?\\d+(?:\\.\\d+)? -?\\d+(?:\\.\\d+)? -?\\d+(?:\\.\\d+)?$");
    }

    @Unique
    private static boolean isBlockPos(String string) {
        return string.matches("^-?\\d+ -?\\d+ -?\\d+$");
    }

    @Unique
    private static boolean isUUID(String string) {
        return string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}
