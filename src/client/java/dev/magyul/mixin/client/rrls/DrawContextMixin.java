package dev.magyul.mixin.client.rrls;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/GuiAtlasManager;getSprite(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/Sprite;"))
    private Sprite fixSpriteCrash(GuiAtlasManager instance, Identifier location, Operation<Sprite> original) {
        try {
            return original.call(instance, location);
        } catch (Throwable var5) {
            return null;
        }
    }

    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/GuiAtlasManager;getScaling(Lnet/minecraft/client/texture/Sprite;)Lnet/minecraft/client/texture/Scaling;"))
    public Scaling fixSpriteCrash(GuiAtlasManager instance, Sprite sprite, Operation<Scaling> original) {
        return sprite == null ? null : original.call(instance, sprite);
    }

    @WrapOperation(method = "drawGuiTexture(Lnet/minecraft/util/Identifier;IIIIIIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(Lnet/minecraft/client/texture/Sprite;IIIII)V"))
    public void fixSpriteCrash(DrawContext instance, Sprite sprite, int x, int y, int z, int width, int height, Operation<Void> original) {
        if (sprite != null) {
            original.call(instance, sprite, x, y, z, width, height);
        }
    }
}
