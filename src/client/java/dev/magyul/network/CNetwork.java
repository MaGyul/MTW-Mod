package dev.magyul.network;

import dev.magyul.util.ClientUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CNetwork {

    public static void register() {
        // Use Voice Activity
        ClientPlayNetworking.registerGlobalReceiver(Namespaces.UPDATE_ALL_MIC, CNetwork::receivedUpdateAllMic);
        ClientPlayNetworking.registerGlobalReceiver(Namespaces.PICKUP_REACH, CNetwork::receivedPickupReach);
    }

    private static void receivedUpdateAllMic(UpdateAllMicS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientUtil.setAllMic(packet.value());
    }

    private static void receivedPickupReach(PickupReachS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientUtil.pickupReach = packet.reach();
    }
}
