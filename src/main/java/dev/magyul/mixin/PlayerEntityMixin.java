package dev.magyul.mixin;

import com.mojang.authlib.GameProfile;
import dev.magyul.data.PlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Mutable
    @Shadow @Final private PlayerInventory inventory;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void load(NbtCompound nbt, CallbackInfo cb) {
        PlayerData.load(This(), nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void save(NbtCompound nbt, CallbackInfo cb) {
        PlayerData.save(This(), nbt);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo cb) {
//        inventory = PlayerData.getPlayerInventory(This());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo cb) {
//        if (This() instanceof ServerPlayerEntity serverPlayer) {
//            PlayerInventory playerInventory;
//            if (serverPlayer.interactionManager.getGameMode() == GameMode.ADVENTURE) {
//                playerInventory = PlayerData.getAdventureInventory(serverPlayer);
//            } else {
//                playerInventory = PlayerData.getPlayerInventory(serverPlayer);
//            }
//            if (inventory == playerInventory) return;
//            inventory = playerInventory;
//        }
    }

    @Unique
    private PlayerEntity This() {
        return (PlayerEntity) (Object) this;
    }
}
