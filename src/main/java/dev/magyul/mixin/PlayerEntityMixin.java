package dev.magyul.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.magyul.data.PlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyReturnValue(method = "getName", at = @At("RETURN"))
    private Text getName(Text original) {
        if (This().hasCustomName()) {
            return This().getCustomName();
        }
        return original;
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void load(NbtCompound nbt, CallbackInfo cb) {
        PlayerData.load(This(), nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void save(NbtCompound nbt, CallbackInfo cb) {
        PlayerData.save(This(), nbt);
    }

    @Unique
    private PlayerEntity This() {
        return (PlayerEntity) (Object) this;
    }
}
