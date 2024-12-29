package dev.magyul.mixin;

import dev.magyul.MTWMod;
import dev.magyul.data.WorldData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/PersistentStateManager;getOrCreate(Ljava/util/function/Function;Ljava/util/function/Supplier;Ljava/lang/String;)Lnet/minecraft/world/PersistentState;"))
    private void init(CallbackInfo cb) {
        try {
            var file = getPersistentStateManager().getFile(MTWMod.ID);
            if (file.exists()) {
                WorldData.get(This()).readNbt(NbtIo.readCompressed(file));
            }
        } catch (Exception ex) {
            MTWMod.LOGGER.error("Error loading saved data: {}", MTWMod.ID, ex);
        }
    }

    @Inject(method = "saveLevel", at = @At("TAIL"))
    private void saveLevel(CallbackInfo cb) {
        try {
            var file = getPersistentStateManager().getFile(MTWMod.ID);
            NbtIo.writeCompressed(WorldData.get(This()).writeNbt(new NbtCompound()), file);
        } catch (IOException ex) {
            MTWMod.LOGGER.error("Could not save data {}", MTWMod.ID, ex);
        }
    }

    @Unique
    private ServerWorld This() {
        return (ServerWorld) (Object) this;
    }
}
