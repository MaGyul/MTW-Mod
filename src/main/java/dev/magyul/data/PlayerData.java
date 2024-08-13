package dev.magyul.data;

import dev.magyul.network.UpdateAllMicS2CPacket;
import dev.magyul.util.Vec3dUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private static final Map<UUID, PlayerData> playerData = new HashMap<>();
    @NotNull
    private final PlayerEntity player;
    private int lastTick;
    private boolean allMic = false;
    private ItemStack carryItem = ItemStack.EMPTY;
    private NbtCompound carryState = null;
    private NbtCompound carryTile = null;
    private final Map<Identifier, Vec3d> moveHere = new HashMap<>();

    private PlayerData(@NotNull PlayerEntity player) {
        this.player = player;
    }

    private void load(NbtCompound nbt) {
        allMic = nbt.getBoolean("allMic");
        if (nbt.contains("carryState", NbtElement.COMPOUND_TYPE)) {
            carryState = nbt.getCompound("carryState");
        }
        if (nbt.contains("carryTile", NbtElement.COMPOUND_TYPE)) {
            carryTile = nbt.getCompound("carryTile");
        }
        if (nbt.contains("carryItem", NbtElement.COMPOUND_TYPE)) {
            carryItem = ItemStack.fromNbt(player.getWorld().getRegistryManager(), nbt.get("carryItem"))
                    .orElse(ItemStack.EMPTY);
        }
        if (nbt.contains("moveHere", NbtElement.COMPOUND_TYPE)) {
            moveHere.clear();
            var compound = nbt.getCompound("moveHere");
            for (String key : compound.getKeys()) {
                moveHere.put(new Identifier(key), Vec3dUtil.fromNbtList(nbt.getList(key, NbtElement.DOUBLE_TYPE)));
            }
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
            nbt.put("carryItem", carryItem.encodeAllowEmpty(player.getWorld().getRegistryManager()));
        }
        if (!moveHere.isEmpty()) {
            var compound = new NbtCompound();
            moveHere.forEach((key, value) -> compound.put(key.toString(), Vec3dUtil.toNbtList(value)));
            nbt.put("moveHere", compound);
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

    @NotNull
    public static ItemStack getCarryItem(ServerPlayerEntity player) {
        return get(player).carryItem;
    }

    public static void setCarryItem(ServerPlayerEntity player, @NotNull ItemStack stack) {
        get(player).carryItem = stack.copy();
        get(player).lastTick = player.age;
    }

    @NotNull
    public static Vec3d getMoveHere(ServerPlayerEntity player, ServerWorld world) {
        return get(player).moveHere.getOrDefault(world.getRegistryKey().getValue(), player.getPos());
    }

    public static void setMoveHere(ServerPlayerEntity player, ServerWorld world, @Nullable Vec3d vec3d) {
        if (vec3d == null) {
            get(player).moveHere.remove(world.getRegistryKey().getValue());
        } else {
            get(player).moveHere.put(world.getRegistryKey().getValue(), vec3d);
        }
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
