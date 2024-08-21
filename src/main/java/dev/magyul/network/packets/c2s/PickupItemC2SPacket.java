package dev.magyul.network.packets.c2s;

import dev.magyul.data.PlayerData;
import dev.magyul.network.IPacket;
import dev.magyul.network.PacketType;
import dev.magyul.registers.MTWDataComponentTypes;
import dev.magyul.registers.MTWGameRules;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameMode;

import java.util.UUID;

public record PickupItemC2SPacket(UUID uuid) implements IPacket {
    public static final PacketType<PickupItemC2SPacket> TYPE = PacketType.create("pickup_item", PickupItemC2SPacket::new);

    public PickupItemC2SPacket(RegistryByteBuf buf) {
        this(buf.readUuid());
    }

    @Override
    public void write(RegistryByteBuf buf) {
        buf.writeUuid(uuid);
    }

    @Override
    public void handle(Context context) {
        if (context.side().isServer() && context.player() instanceof ServerPlayerEntity player) {
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
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    private ItemStack addFakeItem(ItemStack stack) {
        stack.set(MTWDataComponentTypes.IS_CARRY, true);
        return stack;
    }
}
