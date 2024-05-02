package dev.magyul.data;

import dev.magyul.inventory.AdventureInventory;
import dev.magyul.network.UpdateAllMicS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {
    private static final Map<UUID, PlayerData> playerData = new HashMap<>();
    @NotNull
    private final PlayerEntity player;
    @NotNull
    private final AdventureInventory adventureInventory;
    @NotNull
    private final PlayerInventory playerInventory;
    private int lastTick;
    private boolean allMic = false;
    private ItemStack carryItem = ItemStack.EMPTY;
    private NbtCompound carryState = null;
    private NbtCompound carryTile = null;

    private PlayerData(@NotNull PlayerEntity player) {
        this.player = player;
        this.adventureInventory = new AdventureInventory(player);
        this.playerInventory = new PlayerInventory(player);
    }

    private void load(NbtCompound nbt) {
        if (nbt.contains("allMic")) {
            allMic = nbt.getBoolean("allMic");
        }
        if (nbt.contains("carryState", NbtElement.COMPOUND_TYPE)) {
            carryState = nbt.getCompound("carryState");
        }
        if (nbt.contains("carryTile", NbtElement.COMPOUND_TYPE)) {
            carryTile = nbt.getCompound("carryTile");
        }
        if (nbt.contains("carryItem", NbtElement.COMPOUND_TYPE)) {
            carryItem = ItemStack.fromNbt(nbt.getCompound("carryItem"));
        }
    }

    private NbtCompound save() {
        var nbt = new NbtCompound();
        nbt.putBoolean("allMic", allMic);
        if (carryState != null) {
            nbt.put("carryState", carryState);
        }
        if (carryTile != null) {
            nbt.put("carryTile", carryTile);
        }
        if (carryItem != null) {
            var item = new NbtCompound();
            carryItem.writeNbt(item);
            nbt.put("carryItem", item);
        }

        return nbt;
    }

    public static PlayerData get(PlayerEntity player) {
        var uuid = player.getUuid();
        if (!playerData.containsKey(uuid)) playerData.put(uuid, new PlayerData(player));
        return playerData.get(uuid);
    }

    public static int lastTick(ServerPlayerEntity player) {
        return get(player).lastTick;
    }

    public static boolean isAllMic(ServerPlayerEntity player) {
        return get(player).allMic;
    }

    public static void setAllMic(ServerPlayerEntity player, boolean value) {
        get(player).allMic = value;
        ServerPlayNetworking.send(player, new UpdateAllMicS2CPacket(value));
    }

    public static boolean hasCarryState(PlayerEntity player) {
        return get(player).carryState != null;
    }

    @NotNull
    public static BlockState getCarryState(PlayerEntity player) {
        var state = get(player).carryState;
        Objects.requireNonNull(state);
        return NbtHelper.toBlockState(player.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), state);
    }

    @Nullable
    public static NbtCompound getCarryTileNBT(ServerPlayerEntity player) {
        return get(player).carryTile;
    }

    @Nullable
    public static BlockEntity getCarryTile(ServerPlayerEntity player, BlockPos pos) {
        var tile = get(player).carryTile;
        if (tile == null) return null;

        return BlockEntity.createFromNbt(pos, getCarryState(player), tile);
    }

    public static void setCarryState(ServerPlayerEntity player, BlockState state, BlockEntity blockEntity) {
        if (state == null) {
            get(player).carryState = null;
        } else {
            get(player).carryState = NbtHelper.fromBlockState(state);
        }
        if (blockEntity == null) {
            get(player).carryTile = null;
        } else {
            get(player).carryTile = blockEntity.createNbtWithId();
        }
        get(player).lastTick = player.age;
    }

    @NotNull
    public static ItemStack getCarryItem(ServerPlayerEntity player) {
        return get(player).carryItem;
    }

    public static void setCarryItem(ServerPlayerEntity player, @NotNull ItemStack stack) {
        get(player).carryItem = stack.copy();
        get(player).lastTick = player.age;
    }

    public static PlayerInventory getAdventureInventory(PlayerEntity player) {
        return get(player).adventureInventory;
    }

    public static PlayerInventory getPlayerInventory(PlayerEntity player) {
        return get(player).playerInventory;
    }

    public static void load(PlayerEntity player, NbtCompound nbt) {
        if (nbt.contains("MTWMod")) {
            get(player).load(nbt.getCompound("MTWMod"));
        } else {
            get(player).load(new NbtCompound());
        }
    }

    public static void save(PlayerEntity player, NbtCompound nbt) {
        nbt.put("MTWMod", get(player).save());
    }
}
