package dev.magyul.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.magyul.network.packets.s2c.PickupReachS2CPacket;
import dev.magyul.registers.MTWGameRules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract boolean isOperator(GameProfile profile);

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 4, shift = At.Shift.AFTER))
    private void onPlayerConnectPacket(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        var world = player.getServerWorld();
        var reach = world.getGameRules().getInt(MTWGameRules.PICKUP_REACH);
        ServerPlayNetworking.send(player, new PickupReachS2CPacket(reach));
    }

    @WrapOperation(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    private ServerWorld onJoinOverworld(MinecraftServer instance, RegistryKey<World> key, Operation<ServerWorld> original, ClientConnection connection, ServerPlayerEntity player) {
        if (isNotOp(player) && key != World.OVERWORLD) {
            key = World.OVERWORLD;
        }

        return original.call(instance, key);
    }

    @Unique
    private boolean isNotOp(ServerPlayerEntity player) {
        return !isOperator(player.getGameProfile());
    }
}

