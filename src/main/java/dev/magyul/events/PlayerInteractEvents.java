package dev.magyul.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerInteractEvents {
    public static final Event<AttackAir> ATTACK_AIR_EVENT = EventFactory.createArrayBacked(AttackAir.class,
            (listeners) -> player -> {
                for (var listener : listeners) {
                    listener.attack(player);
                }
            });
    public static final Event<LeftClickEmpty> LEFT_CLICK_EMPTY = EventFactory.createArrayBacked(LeftClickEmpty.class,
            (listeners) -> (player, hand, pos) -> {
                for (var listener : listeners) {
                    listener.click(player, hand, pos);
                }
            });
    public static final Event<RightClickEmpty> RIGHT_CLICK_EMPTY = EventFactory.createArrayBacked(RightClickEmpty.class,
            (listeners) -> (player, hand, pos) -> {
                for (var listener : listeners) {
                    listener.click(player, hand, pos);
                }
            });

    public interface AttackAir {
        void attack(ServerPlayerEntity player);
    }

    public interface LeftClickEmpty {
        void click(PlayerEntity player, Hand hand, BlockPos pos);
    }

    public interface RightClickEmpty {
        void click(PlayerEntity player, Hand hand, BlockPos pos);
    }
}
