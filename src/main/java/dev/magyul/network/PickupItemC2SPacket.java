package dev.magyul.network;

import dev.magyul.MTWMod;
import dev.magyul.data.PlayerData;
import dev.magyul.registers.MTWGameRules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.UUID;

public record PickupItemC2SPacket(UUID uuid) implements FabricPacket {
    public static final PacketType<PickupItemC2SPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "pickup_item"), PickupItemC2SPacket::new);

    public PickupItemC2SPacket(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(uuid);
    }

    public void receive(ServerPlayerEntity player, PacketSender ignoredSender) {
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
                inventory.armor.set(3, PlayerData.getCarryItem(player));

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

    /*
    var pos = blockHit.getBlockPos();
    if (!ServerUtil.canCarryGeneral(serverPlayer, Vec3d.ofCenter(pos))) return ActionResult.PASS;
    var target = world.getBlockState(pos);
    if (!target.isIn(MTWTags.CARRY_ON)) return ActionResult.PASS;
    var blockEntity = world.getBlockEntity(pos);

    if (blockEntity != null) {
        var nbt = blockEntity.createNbtWithId();
        if (nbt.contains("Lock") && !nbt.getString("Lock").isEmpty())
            return ActionResult.PASS;
    }

    var doPickup = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos, target, blockEntity);
    if (!doPickup) return ActionResult.PASS;

    PlayerData.setCarryState(serverPlayer, target, blockEntity);

    var item = target.getBlock().asItem();
    inventory.armor.set(3, item.getDefaultStack());

    world.removeBlockEntity(pos);
    world.removeBlock(pos, false);
    serverPlayer.currentScreenHandler.sendContentUpdates();
    return ActionResult.SUCCESS;
     */

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
