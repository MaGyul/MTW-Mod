package dev.magyul.mixin;


import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin({PlayerManager.class})
public abstract class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Shadow @Final private WorldSaveHandler saveHandler;

    @Shadow public abstract boolean isOperator(GameProfile profile);

    public PlayerManagerMixin() {
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setGameMode(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        if (!isOp(player) && player.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            var world = player.getServerWorld();
            player.setPosition(world.getSpawnPos().toCenterPos());
            player.setYaw(world.getSpawnAngle());
            player.setPitch(0f);
        }
    }

    @Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void respawnPlayer(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld, Optional optional, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity, boolean bl2, byte b) {
        if (!isOp(serverPlayerEntity) && serverPlayerEntity.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            var world = serverPlayerEntity.getServerWorld();
            player.setPosition(world.getSpawnPos().toCenterPos());
            serverPlayerEntity.setYaw(world.getSpawnAngle());
            serverPlayerEntity.setPitch(0f);
        }
    }

//    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getOverworld()Lnet/minecraft/server/world/ServerWorld;"))
//    private ServerWorld respawnPos(MinecraftServer server, ServerPlayerEntity player) {
//        if (player.interactionManager.getGameMode() == GameMode.ADVENTURE) {
//            return player.getServerWorld();
//        }
//        return server.getOverworld();
//    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private ServerWorld onJoinOverworld(MinecraftServer server, RegistryKey<World> key, ClientConnection connection, ServerPlayerEntity player) {
        if (!isOp(player)) {
            var nbt = mtw$loadPlayerData(player);
            var gameMode = getServerGameMode(gameModeFromNbt(nbt));
            if (gameMode == GameMode.ADVENTURE && key != World.OVERWORLD) {
                key = World.OVERWORLD;
            }
        }

        return server.getWorld(key);
    }

    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private ServerWorld respawnOverworld(MinecraftServer server, RegistryKey<World> key, ServerPlayerEntity player) {
        if (!isOp(player) && player.interactionManager.getGameMode() == GameMode.ADVENTURE) {
            return server.getOverworld();
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
    @Nullable
    public NbtCompound mtw$loadPlayerData(ServerPlayerEntity player) {
        NbtCompound nbtCompound = this.server.getSaveProperties().getPlayerData();
        NbtCompound nbtCompound2;
        if (this.server.isHost(player.getGameProfile()) && nbtCompound != null) {
            nbtCompound2 = nbtCompound;
        } else {
            nbtCompound2 = this.saveHandler.loadPlayerData(player);
        }

        return nbtCompound2;
    }

    @Unique
    private boolean isOp(ServerPlayerEntity player) {
        return isOperator(player.getGameProfile());
    }
}

