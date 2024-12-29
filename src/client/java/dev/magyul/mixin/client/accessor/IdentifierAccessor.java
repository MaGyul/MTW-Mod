package dev.magyul.mixin.client.accessor;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Identifier.class)
public interface IdentifierAccessor {
    @Final
    @Mutable
    @Accessor
    void setNamespace(String namespace);

    @Final
    @Mutable
    @Accessor
    void setPath(String path);
}
