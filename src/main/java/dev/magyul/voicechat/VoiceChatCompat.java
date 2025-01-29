package dev.magyul.voicechat;

import de.maxhenkel.voicechat.voice.client.ClientManager;
import net.fabricmc.loader.api.FabricLoader;

import java.util.UUID;

public class VoiceChatCompat {

    public static boolean isTalking(UUID uuid) {
        if (!FabricLoader.getInstance().isModLoaded("voicechat")) {
            return false;
        }
        var clientVoicechat = ClientManager.getClient();
        if (clientVoicechat == null) {
            return false;
        }
        return clientVoicechat.getTalkCache().isTalking(uuid);
    }
}
