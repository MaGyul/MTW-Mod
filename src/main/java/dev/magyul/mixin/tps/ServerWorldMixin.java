package dev.magyul.mixin.tps;

import dev.magyul.api.TickTime;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements TickTime {
    @Unique
    private long tickStart = 0;
    @Unique
    private float tickTime = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickStart(CallbackInfo cb) {
        tickStart = Util.getMeasuringTimeNano();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tickEnd(CallbackInfo cb) {
        var tickDuration = Util.getMeasuringTimeNano() - tickStart;
        tickTime = tickTime * 0.8F + (float)tickDuration / 1000000.0F * 0.19999999F;
    }

    @Override
    public float mtw$tickTime() {
        return tickTime;
    }
}
