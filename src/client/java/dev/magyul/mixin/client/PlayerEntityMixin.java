package dev.magyul.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.magyul.data.PlayerData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo cb) {
//        inventory = PlayerData.getPlayerInventory(This());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo cb) {
//        if (This() instanceof ClientPlayerEntity clientPlayer) {
//            var entry = clientPlayer.getPlayerListEntry();
//            if (entry != null) {
//                PlayerInventory playerInventory;
//                if (entry.getGameMode() == GameMode.ADVENTURE) {
//                    playerInventory = PlayerData.getAdventureInventory(clientPlayer);
//                } else {
//                    playerInventory = PlayerData.getPlayerInventory(clientPlayer);
//                }
//                if (inventory == playerInventory) return;
//                inventory = playerInventory;
//            }
//        }
    }

    @Unique
    private PlayerEntity This() {
        return (PlayerEntity) (Object) this;
    }
}
