package dev.magyul.mixin;

import dev.magyul.MTWMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetWorldSpawnCommand;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetWorldSpawnCommand.class)
public class SetWorldSpawnCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void execute(ServerCommandSource source, BlockPos pos, float angle, CallbackInfoReturnable<Integer> cir) {
//        MTWMod.LOGGER.info("{}", source.getWorld().getRegistryKey().getValue());
        source.getWorld().setSpawnPos(pos, angle);
//        var spawnPos = source.getWorld().getSpawnPos();
//        source.sendMessage(Text.literal("[r] ").append(Text.translatable("commands.setworldspawn.success", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), source.getWorld().getSpawnAngle())));
        source.sendFeedback(() -> {
            return Text.translatable("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), angle);
        }, true);
        cir.setReturnValue(1);
    }
}
