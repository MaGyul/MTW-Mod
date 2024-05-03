package dev.magyul.mixin;

import dev.magyul.MTWMod;
import dev.magyul.data.WorldData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/PersistentStateManager;getOrCreate(Lnet/minecraft/world/PersistentState$Type;Ljava/lang/String;)Lnet/minecraft/world/PersistentState;"))
    private void init(CallbackInfo cb) {
        try {
            var file = getPersistentStateManager().getFile(MTWMod.ID);
            if (file.exists()) {
                WorldData.get(This()).readNbt(readNbt(file));
            }
        } catch (Exception ex) {
            MTWMod.LOGGER.error("Error loading saved data: {}", MTWMod.ID, ex);
        }
    }

    @Inject(method = "saveLevel", at = @At("TAIL"))
    private void saveLevel(CallbackInfo cb) {
        try {
            var file = getPersistentStateManager().getFile(MTWMod.ID);
            NbtIo.writeCompressed(WorldData.get(This()).writeNbt(new NbtCompound()), file.toPath());
        } catch (IOException ex) {
            MTWMod.LOGGER.error("Could not save data {}", MTWMod.ID, ex);
        }
    }

    @Unique
    public NbtCompound readNbt(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        NbtCompound var9;
        try {
            PushbackInputStream pushbackInputStream = new PushbackInputStream(fileInputStream, 2);

            try {
                NbtCompound nbtCompound;
                if (this.isCompressed(pushbackInputStream)) {
                    nbtCompound = NbtIo.readCompressed(pushbackInputStream, NbtSizeTracker.ofUnlimitedBytes());
                } else {
                    DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);

                    try {
                        nbtCompound = NbtIo.readCompound(dataInputStream);
                    } catch (Throwable var14) {
                        try {
                            dataInputStream.close();
                        } catch (Throwable var13) {
                            var14.addSuppressed(var13);
                        }

                        throw var14;
                    }

                    dataInputStream.close();
                }

                var9 = nbtCompound;
            } catch (Throwable var15) {
                try {
                    pushbackInputStream.close();
                } catch (Throwable var12) {
                    var15.addSuppressed(var12);
                }

                throw var15;
            }

            pushbackInputStream.close();
        } catch (Throwable var16) {
            try {
                fileInputStream.close();
            } catch (Throwable var11) {
                var16.addSuppressed(var11);
            }

            throw var16;
        }

        fileInputStream.close();
        return var9;
    }

    @Unique
    private boolean isCompressed(PushbackInputStream stream) throws IOException {
        byte[] bs = new byte[2];
        boolean bl = false;
        int i = stream.read(bs, 0, 2);
        if (i == 2) {
            int j = (bs[1] & 255) << 8 | bs[0] & 255;
            if (j == 35615) {
                bl = true;
            }
        }

        if (i != 0) {
            stream.unread(bs, 0, i);
        }

        return bl;
    }

    @Unique
    private ServerWorld This() {
        return (ServerWorld) (Object) this;
    }
}
