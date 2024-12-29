package dev.magyul.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.magyul.network.packets.s2c.PickupReachS2CPacket;
import dev.magyul.registers.MTWGameRules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract boolean isOperator(GameProfile profile);

    @Shadow @Nullable public abstract NbtCompound loadPlayerData(ServerPlayerEntity player);

    public PlayerManagerMixin() {
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setGameMode(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (isNotOp(player) && player.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            var world = player.getServerWorld();
            player.setPosition(world.getSpawnPos().toCenterPos());
            player.setYaw(world.getSpawnAngle());
            player.setPitch(0f);
        }
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 4, shift = At.Shift.AFTER))
    private void onPlayerConnectPacket(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        var world = player.getServerWorld();
        var reach = world.getGameRules().getInt(MTWGameRules.PICKUP_REACH);
        ServerPlayNetworking.send(player, new PickupReachS2CPacket(reach));
    }

    @WrapOperation(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getSpawnPointPosition()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos respawnPlayerPos(ServerPlayerEntity instance, Operation<BlockPos> original, ServerPlayerEntity player, boolean alive) {
        if (player.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            var world = server.getOverworld();
            return world.getSpawnPos();
        }
        return original.call(instance);
    }

    @WrapOperation(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private ServerWorld respawnPlayerWorld(MinecraftServer instance, RegistryKey<World> key, Operation<ServerWorld> original, ServerPlayerEntity player, boolean alive) {
        if (player.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            return server.getOverworld();
        }
        return original.call(instance, key);
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private ServerWorld onJoinOverworld(MinecraftServer server, RegistryKey<World> key, ClientConnection connection, ServerPlayerEntity player) {
        if (isNotOp(player)) {
            var nbt = loadPlayerData(player);
            var gameMode = getServerGameMode(gameModeFromNbt(nbt));
            if (gameMode == GameMode.ADVENTURE && key != World.OVERWORLD) {
                key = World.OVERWORLD;
            }
        }

        return server.getWorld(key);
    }

    @Unique
    @Nullable
    private static GameMode gameModeFromNbt(@Nullable NbtCompound nbt) {
        return nbt != null && nbt.contains("playerGameType", 99) ?
                GameMode.byId(nbt.getInt("playerGameType")) : null;
    }

    @Unique
    private GameMode getServerGameMode(@Nullable GameMode backupGameMode) {
        GameMode gameMode = server.getForcedGameMode();
        if (gameMode != null) {
            return gameMode;
        } else {
            return backupGameMode != null ? backupGameMode : server.getDefaultGameMode();
        }
    }

    @Unique
    private boolean isNotOp(ServerPlayerEntity player) {
        return !isOperator(player.getGameProfile());
    }
}

