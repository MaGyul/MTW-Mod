package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.magyul.MTWMod;
import dev.magyul.MTWModClient;
import dev.magyul.ServerPingPong;
import dev.magyul.events.PlayerInteractEvents;
import dev.magyul.network.packets.c2s.PickupItemC2SPacket;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.ItemUtil;
import dev.magyul.util.OverlayStateHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Nullable public Entity cameraEntity;

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow @Nullable public ClientWorld world;

    @Shadow protected abstract void showResourceReloadFailureToast(@Nullable Text description);

    @Shadow protected abstract CompletableFuture<Void> reloadResources(boolean force, @Nullable MinecraftClient.LoadingContext loadingContext);

    @Inject(method = "onResourceReloadFailure", at = @At("HEAD"), cancellable = true)
    private void onResourceReloadFailure(Throwable exception, Text resourceName, MinecraftClient.LoadingContext loadingContext, CallbackInfo ci) {
        MTWMod.LOGGER.info("Caught error loading resourcepacks!", exception);
        reloadResources(true, loadingContext).thenRun(() ->
                showResourceReloadFailureToast(resourceName));
        ci.cancel();
    }

    @WrapOperation(method = {"tick", "handleInputEvents", "startIntegratedServer"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;"))
    private Overlay mtwmod$render(MinecraftClient instance, Operation<Overlay> original) {
        Overlay overlay = original.call(instance);
        return OverlayStateHelper.isRendering(overlay) ? null : overlay;
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z", ordinal = 4))
    private boolean handleInputEvents(KeyBinding instance) {
        if (player != null) {
            var entry = player.getPlayerListEntry();
            if (entry != null) {
                if (entry.getGameMode() == GameMode.ADVENTURE) {
                    return false;
                }
            }
        }
        return instance.wasPressed();
    }

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;resetLastAttackedTicks()V", shift = At.Shift.AFTER))
    private void doAttack(CallbackInfoReturnable<Boolean> cb) {
        if (player != null) {
            PlayerInteractEvents.LEFT_CLICK_EMPTY.invoker().click(player, Hand.MAIN_HAND, player.getBlockPos());
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", shift = At.Shift.BEFORE, ordinal = 1))
    private void doItemUse(CallbackInfo ci, @Local Hand hand, @Local ItemStack itemStack) {
        if (player != null) {
            if (itemStack.isEmpty() && (crosshairTarget == null || crosshairTarget.getType() == HitResult.Type.MISS)) {
                PlayerInteractEvents.RIGHT_CLICK_EMPTY.invoker().click(player, hand, player.getBlockPos());
            }
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z", shift = At.Shift.AFTER), cancellable = true)
    private void tryPickupItem(CallbackInfo cb) {
        if (cameraEntity != null && interactionManager != null && player != null && world != null && interactionManager.getCurrentGameMode() == GameMode.ADVENTURE) {
            var entity = ItemUtil.raycastItem(cameraEntity, ClientUtil.pickupReach / 10f);
            if (entity != null) {
                ClientPlayNetworking.send(new PickupItemC2SPacket(entity.getUuid()));
                player.swingHand(Hand.MAIN_HAND);
                cb.cancel();
            }
        }
    }

    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen injected(Screen screen) {
        if (screen instanceof TitleScreen) {
            ServerPingPong.serverJoined = false;
        } else if (screen instanceof MultiplayerScreen) {
            screen = new TitleScreen();
        } else if (screen instanceof ProgressScreen) {
            ClientUtil.cs = null;
        }
        if (screen == null) {
            ClientUtil.cs = null;
        }

        MTWModClient.instance.onChangeScreen(screen);

        return screen;
    }
}
