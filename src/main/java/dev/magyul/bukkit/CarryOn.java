package dev.magyul.bukkit;

import dev.magyul.MTWMod;
import dev.magyul.bukkit.events.CarryOnDropItemEvent;
import dev.magyul.bukkit.events.CarryOnPickupItemEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@SuppressWarnings("LoggingSimilarMessage")
public class CarryOn {

    public static ItemStack pickup(PlayerEntity player, ItemStack stack) {
        var event = new CarryOnPickupItemEvent(BukkitUtils.convertBukkitPlayer(player), BukkitUtils.convertBukkitItemStack(stack));
        if (BukkitUtils.callEvent(event)) return null;
        var event_stack = BukkitUtils.restoreBukkitItemStack(event.getItemStack());
        if (event_stack.isEmpty()) {
            MTWMod.LOGGER.error("[Bukkit] CarryOnPickupItemEvent에 아이템이 비어있습니다. (item: air)");
            MTWMod.LOGGER.error("[Bukkit] 이벤트 캔슬을 이용해주세요!");
            return null;
        }
        return event_stack;
    }

    public static ItemStack drop(PlayerEntity player, ItemStack stack) {
        var event = new CarryOnDropItemEvent(BukkitUtils.convertBukkitPlayer(player), BukkitUtils.convertBukkitItemStack(stack));
        if (BukkitUtils.callEvent(event)) return null;
        stack = BukkitUtils.restoreBukkitItemStack(event.getItemStack());
        if (stack.isEmpty()) {
            MTWMod.LOGGER.error("[Bukkit] CarryOnDropItemEvent에 아이템이 비어있습니다. (item: air)");
            MTWMod.LOGGER.error("[Bukkit] 이벤트 캔슬을 이용해주세요!");
            return null;
        }

        return stack;
    }
}
