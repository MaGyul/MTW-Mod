package dev.magyul.registers;

import dev.magyul.network.packets.s2c.PickupReachS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

public class MTWGameRules {
    public static GameRules.Key<GameRules.BooleanRule> DO_PICKUP_MODE;
    public static GameRules.Key<GameRules.IntRule> PICKUP_REACH;

    public static void register(RegisterMethod rm) {
        DO_PICKUP_MODE = rm.register("doPickupMode", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false));
        PICKUP_REACH = rm.register("pickupReach", GameRules.Category.PLAYER, GameRules.IntRule.create(45, (server, rule) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(player, new PickupReachS2CPacket(rule.get()));
            }
        }));
    }

    public interface RegisterMethod {
        <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type);
    }
}
