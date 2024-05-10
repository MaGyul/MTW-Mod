package dev.magyul.network;

import dev.magyul.data.PlayerData;
import dev.magyul.registers.MTWDataComponentTypes;
import dev.magyul.registers.MTWGameRules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameMode;

import java.util.UUID;

public record PickupItemC2SPacket(UUID uuid) implements CustomPayload {

    public PickupItemC2SPacket(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(uuid);
    }

    public void receive(ServerPlayNetworking.Context context) {
        var player = context.player();
        if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) return;
        var server = player.getServerWorld();
        var world = player.getServerWorld();
        var entity = world.getEntity(uuid);
        var inventory = player.getInventory();
        if (entity instanceof ItemEntity itemE) {
            var stack = itemE.getStack();
            var doPickupMode = server.getGameRules().get(MTWGameRules.DO_PICKUP_MODE);

            if (stack.getItem() instanceof BlockItem && !doPickupMode.get()) {
                if (!PlayerData.getCarryItem(player).isEmpty()) return;
                PlayerData.setCarryItem(player, stack.copyWithCount(1));
                inventory.armor.set(3, addFakeItem(stack.copyWithCount(1)));

                stack.decrement(1);
                if (stack.isEmpty()) {
                    itemE.discard();
                }

                player.currentScreenHandler.sendContentUpdates();
                return;
            }
            ItemStack itemStack = itemE.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if (itemE.pickupDelay == 0 && (itemE.owner == null || itemE.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                player.sendPickup(itemE, i);
                if (itemStack.isEmpty()) {
                    itemE.discard();
                    itemStack.setCount(i);
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                player.triggerItemPickedUpByEntityCriteria(itemE);
            }
        }
    }

    private ItemStack addFakeItem(ItemStack stack) {
        stack.set(MTWDataComponentTypes.IS_CARRY, true);
        return stack;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return Namespaces.PICKUP_ITEM;
    }
}
