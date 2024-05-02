package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.event.ModMenuEventHandler;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import com.terraformersmc.modmenu.gui.widget.UpdateCheckerTexturedButtonWidget;
import dev.magyul.MTWMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    @Shadow @Final private static int GRID_MARGIN;

    @Shadow @Final private static int GRID_COLUMNS;

    @Shadow @Final private static Text RETURN_TO_GAME_TEXT;

    @Shadow protected abstract ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier);

    @Shadow @Final private static Text ADVANCEMENTS_TEXT;

    @Shadow @Final private static Text STATS_TEXT;

    @Shadow @Final private static Text OPTIONS_TEXT;

    @Shadow @Final private static Text SHARE_TO_LAN_TEXT;

    @Shadow @Final private static Text PLAYER_REPORTING_TEXT;

    @Shadow @Nullable private ButtonWidget exitButton;

    @Shadow @Final private static Text RETURN_TO_MENU_TEXT;

    @Shadow @Final private static Text SAVING_LEVEL_TEXT;

    @Mutable
    @Shadow @Final private static Text GAME_TEXT;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injected(CallbackInfo ci) {
        GAME_TEXT = Text.translatable("mtwmod.menu.game", MTWMod.VERSION);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget;refreshPositions()V", shift = At.Shift.AFTER))
    private void injected(CallbackInfo cb, @Local LocalRef<GridWidget> localRef) {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(GRID_MARGIN, GRID_MARGIN, GRID_MARGIN, 0);
        var adder = gridWidget.createAdder(GRID_COLUMNS);
        adder.add(ButtonWidget.builder(RETURN_TO_GAME_TEXT, button -> {
            client.setScreen(null);
            client.mouse.lockCursor();
        }).width(204).build(), GRID_COLUMNS, gridWidget.copyPositioner().marginTop(50));
        adder.add(createButton(ADVANCEMENTS_TEXT, () ->
                new AdvancementsScreen(client.player.networkHandler.getAdvancementHandler())
        ));
        adder.add(createButton(STATS_TEXT, () ->
                new StatsScreen(this, client.player.getStatHandler())
        ));
        adder.add(createButton(OPTIONS_TEXT, () ->
                new OptionsScreen(this, client.options)
        ));
        if (client.isIntegratedServerRunning() && !client.getServer().isRemote()) {
            adder.add(createButton(SHARE_TO_LAN_TEXT, () ->
                    new OpenToLanScreen(this)
            ));
        } else {
            adder.add(createButton(PLAYER_REPORTING_TEXT, SocialInteractionsScreen::new));
        }
        adder.add(new ModMenuButtonWidget(0, 0, 204, 20, ModMenuApi.createModsButtonText(), this),
                GRID_COLUMNS);

        Text text = client.isInSingleplayer() ? RETURN_TO_MENU_TEXT : Text.translatable("gui.toTitle");
        exitButton = adder.add(ButtonWidget.builder(text, button -> {
            button.active = false;
            client.getAbuseReportContext().tryShowDraftScreen(client, this, this::disconnect, true);
        }).width(204).build(), GRID_COLUMNS);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5F, 0.25F);
        localRef.set(gridWidget);
    }

    @SuppressWarnings("DataFlowIssue")
    @Unique
    private void disconnect() {
        client.world.disconnect();
        if (client.isInSingleplayer()) {
            client.disconnect(new MessageScreen(SAVING_LEVEL_TEXT));
        } else {
            client.disconnect();
        }

        client.setScreen(new TitleScreen());
    }
}
