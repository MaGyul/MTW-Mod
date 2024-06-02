package dev.magyul.mixin.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.magyul.commands.arguments.PlayerNameArgumentType;
import dev.magyul.commands.arguments.RegionFirstArgument;
import dev.magyul.commands.arguments.RegionSecondArgument;
import net.fabricmc.fabric.impl.gametest.FabricGameTestHelper;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.TestClassArgumentType;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {
    @Shadow
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String string, Class<? extends A> clazz, ArgumentSerializer<A, T> argumentSerializer) {
        throw new AssertionError("Nope.");
    }

    @Inject(method = "register(Lnet/minecraft/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", at = @At("RETURN"))
    private static void register(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> cb) {
        register(registry, "region:first", RegionFirstArgument.class, ConstantArgumentSerializer.of(RegionFirstArgument::first));
        register(registry, "region:second", RegionSecondArgument.class, ConstantArgumentSerializer.of(RegionSecondArgument::second));
        register(registry, "brigadier:playername", PlayerNameArgumentType.class, ConstantArgumentSerializer.of(PlayerNameArgumentType::pn));
    }
}
