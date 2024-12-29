package dev.magyul.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.magyul.MTWMod;
import dev.magyul.ServerPingPong;
import dev.magyul.util.ConnectServer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.AccessibilityOnboardingButtons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

import static dev.magyul.util.ClientUtil.*;

@SuppressWarnings("DataFlowIssue")
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
//    @Unique
//    private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
//    @Unique
//    private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
    @Unique
    private static final Identifier ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
//    @Unique
//    private static final Text LAN_SCANNING_TEXT = Text.translatable("lanServer.scanning");
//    @Unique
//    private static final Text CANNOT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").styled((style) -> style.withColor(-65536));
//    @Unique
//    private static final Text CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").styled((style) -> style.withColor(-65536));
    @Unique
    private static final Text INCOMPATIBLE_TEXT = Text.translatable("multiplayer.status.incompatible");
    @Unique
    private static final Text NO_CONNECTION_TEXT = Text.translatable("multiplayer.status.no_connection");
    @Unique
    private static final Text PINGING_TEXT = Text.translatable("multiplayer.status.pinging");
//    @Unique
//    private static final Text ONLINE_TEXT = Text.translatable("multiplayer.status.online");
    @Shadow @Nullable private SplashTextRenderer splashText;
    @Shadow @Final private static Text COPYRIGHT;

    @Shadow @Nullable protected abstract Text getMultiplayerDisabledText();

    @Shadow protected abstract boolean canReadDemoWorldData();

    @Shadow @Final private static Logger LOGGER;
    @Unique
    private ButtonWidget play;
    @Unique
    private ButtonWidget cancel;
    @Unique
    @Nullable
    private List<Text> playerListSummary;
    @Unique
    @Nullable
    private Identifier statusIconTexture;
    @Unique
    @Nullable
    private Text statusTooltipText;

    private TitleScreenMixin() {
        super(Text.translatable("narrator.screen.title"));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo cb) {
        if (cs == null) {
            if (play != null) play.setMessage(Text.translatable("mtwclient.menu.play"));
            if (cancel != null) {
                remove(cancel);
                cancel = null;
            }
        } else {
            if (play != null && playStatus != null) play.setMessage(playStatus);
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo cb) {
        cb.cancel();
        if (playStatus == null || cs == null) playStatus = Text.translatable("mtwclient.menu.play");

        if (splashText == null) {
            splashText = client.getSplashTextLoader().get();
        }

        int height = this.height / 4 + (48 + 12);
        var text = getMultiplayerDisabledText();
        boolean flag = text == null;

        var tooltip = text != null ? Tooltip.of(text) : null;
        play = addDrawableChild(ButtonWidget.builder(playStatus, (button) -> {
            if (checkTest()) {
                if (canReadDemoWorldData()) {
                    client.createIntegratedServerLoader().start(this, "Demo_World");
                } else {
                    client.createIntegratedServerLoader().createAndStart("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions);
                }
                return;
            }
            if (client.options.skipMultiplayerWarning) {
                cs = ConnectServer.startConnecting(client, this, mtw_address, mtw_info);
                client.setScreen(this);
            } else {
                client.setScreen(new MultiplayerWarningScreen(this));
            }
        }).dimensions(this.width / 2 - 100, height, 200, 20).tooltip(tooltip).build());
        if (cs != null) {
            text = Text.translatable("gui.cancel");
            cancel = addDrawableChild(ButtonWidget.builder(text, (button) -> {
                if (cs != null) {
                    cs.cancel = true;
                }
                play.active = flag;
            }).dimensions(this.width / 2 + 104, height, Math.max(20, textRenderer.getWidth(text)), 20).build());
            play.active = false;
        } else {
            play.active = flag;
        }
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.online"), (button) -> {
        }).dimensions(this.width / 2 - 100, height, 0, 0).build());

        addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, height + 48 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256,
                (button) -> this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())), Text.translatable("narrator.button.language")));
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), (button) ->
                client.setScreen(new OptionsScreen(this, client.options))
        ).dimensions(this.width / 2 - 100, height + 48 + 12, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), (button) ->
                client.scheduleStop()
        ).dimensions(this.width / 2 + 2, height + 48 + 12, 98, 20).build());
        addDrawableChild(new TexturedButtonWidget(this.width / 2 + 104, height + 48 + 12, 20, 20, 0, 0, 20, ButtonWidget.ACCESSIBILITY_TEXTURE, 32, 64,
                (button) -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), Text.translatable("narrator.button.accessibility")));
        int fontWidth = textRenderer.getWidth(COPYRIGHT);
        int width = this.width - fontWidth - 2;
        addDrawableChild(new PressableTextWidget(width, this.height - 10, fontWidth, 10, COPYRIGHT, (button) ->
                client.setScreen(new CreditsAndAttributionScreen(this)), textRenderer)
        );
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = 2000.0F, ordinal = 0), require = 0)
    private float changeAnimationSpeed(float constant) {
        return 1000.0F * 2.0F;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", shift = At.Shift.BEFORE))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local(ordinal = 2) int i) {
        context.drawTextWithShadow(textRenderer, getGameTitle() + " " + MTWMod.VERSION, 2, height - 20, 16777215 | i);

        if (!mtw_info.online) {
            mtw_info.online = true;
            mtw_info.ping = -2L;
            mtw_info.label = ScreenTexts.EMPTY;
            mtw_info.playerCountLabel = ScreenTexts.EMPTY;
            ServerPingPong.startPinging(client, () -> {}, () -> {});
        }

        int width = this.width / 2 + 104; // playButton width ended
        int height = (this.height / 4 + (48 + 12)) - 22;
        boolean bl = !(mtw_info.protocolVersion == SharedConstants.getGameVersion().getProtocolVersion());
        var text = bl ? mtw_info.version.copy().formatted(Formatting.RED) : mtw_info.playerCountLabel;
        int j = textRenderer.getWidth(text);
        context.drawText(textRenderer, text, width - j - 15 - 2, height + 1, -8355712, false);
        int k = 0;
        int l;
        List<Text> playerList;
        Text status;
        if (bl) {
            l = 5;
            status = INCOMPATIBLE_TEXT;
            playerList = mtw_info.playerListSummary;
        } else if (this.pinged()) {
            if (mtw_info.ping < 0L) {
                l = 5;
            } else if (mtw_info.ping < 150L) {
                l = 0;
            } else if (mtw_info.ping < 300L) {
                l = 1;
            } else if (mtw_info.ping < 600L) {
                l = 2;
            } else if (mtw_info.ping < 1000L) {
                l = 3;
            } else {
                l = 4;
            }

            if (mtw_info.ping < 0L) {
                status = NO_CONNECTION_TEXT;
                playerList = Collections.emptyList();
            } else {
                status = Text.translatable("multiplayer.status.ping", new Object[]{mtw_info.ping});
                playerList = mtw_info.playerListSummary;
            }
        } else {
            k = 1;
            l = (int)(Util.getMeasuringTimeMs() / 100L & 7L);
            if (l > 4) {
                l = 8 - l;
            }

            status = PINGING_TEXT;
            playerList = Collections.emptyList();
        }

        context.drawTexture(ICONS_TEXTURE, width - 15, height, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
        var m = mouseY - height;
        if (mouseX >= width - 15 && mouseX <= width - 5 && m >= 0 && m <= 8) {
            context.drawTooltip(textRenderer, List.of(status), mouseX, mouseY);
        } else if (mouseX >= width - j - 15 - 2 && mouseX <= width - 15 - 2 && m >= 0 && m <= 8) {
            context.drawTooltip(textRenderer, playerList, mouseX, mouseY);
        }
    }

    @Unique
    private boolean pinged() {
        return mtw_info.online && mtw_info.ping != -2L;
    }

    @Unique
    private String getGameTitle() {
        if (I18n.hasTranslation("game.title")) {
            return I18n.translate("game.title");
        }
        return "Cheonnyeon Story";
    }
}
