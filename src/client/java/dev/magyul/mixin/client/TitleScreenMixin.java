package dev.magyul.mixin.client;

import com.google.common.collect.Lists;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

import static dev.magyul.util.ClientUtil.*;

@SuppressWarnings("DataFlowIssue")
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier INCOMPATIBLE_TEXTURE = new Identifier("server_list/incompatible");
    @Unique
    private static final Identifier UNREACHABLE_TEXTURE = new Identifier("server_list/unreachable");
    @Unique
    private static final Identifier PING_1_TEXTURE = new Identifier("server_list/ping_1");
    @Unique
    private static final Identifier PING_2_TEXTURE = new Identifier("server_list/ping_2");
    @Unique
    private static final Identifier PING_3_TEXTURE = new Identifier("server_list/ping_3");
    @Unique
    private static final Identifier PING_4_TEXTURE = new Identifier("server_list/ping_4");
    @Unique
    private static final Identifier PING_5_TEXTURE = new Identifier("server_list/ping_5");
    @Unique
    private static final Identifier PINGING_1_TEXTURE = new Identifier("server_list/pinging_1");
    @Unique
    private static final Identifier PINGING_2_TEXTURE = new Identifier("server_list/pinging_2");
    @Unique
    private static final Identifier PINGING_3_TEXTURE = new Identifier("server_list/pinging_3");
    @Unique
    private static final Identifier PINGING_4_TEXTURE = new Identifier("server_list/pinging_4");
    @Unique
    private static final Identifier PINGING_5_TEXTURE = new Identifier("server_list/pinging_5");
    @Unique
    private static final Text INCOMPATIBLE_TEXT = Text.translatable("multiplayer.status.incompatible");
    @Unique
    private static final Text NO_CONNECTION_TEXT = Text.translatable("multiplayer.status.no_connection");
    @Unique
    private static final Text PINGING_TEXT = Text.translatable("multiplayer.status.pinging");
    @Shadow @Nullable private SplashTextRenderer splashText;
    @Shadow @Final private static Text COPYRIGHT;

    @Shadow @Nullable protected abstract Text getMultiplayerDisabledText();

    @Shadow protected abstract boolean canReadDemoWorldData();

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
                    client.createIntegratedServerLoader().start("Demo_World", () -> client.setScreen(this));
                } else {
                    client.createIntegratedServerLoader().createAndStart("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions, this);
                }
                return;
            }
            if (client.options.skipMultiplayerWarning) {
                cs = ConnectServer.startConnecting(client, mtw_address, mtw_info);
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

        var iconBW = addDrawableChild(AccessibilityOnboardingButtons.createLanguageButton(20, (button) ->
                client.setScreen(new LanguageOptionsScreen(this, client.options, client.getLanguageManager())), true)
        );
        iconBW.setPosition(this.width / 2 - 124, height + 48 + 12);
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), (button) ->
                client.setScreen(new OptionsScreen(this, client.options))
        ).dimensions(this.width / 2 - 100, height + 48 + 12, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), (button) ->
                client.scheduleStop()
        ).dimensions(this.width / 2 + 2, height + 48 + 12, 98, 20).build());
        iconBW = addDrawableChild(AccessibilityOnboardingButtons.createAccessibilityButton(20, (button) ->
                client.setScreen(new AccessibilityOptionsScreen(this, client.options)), true)
        );
        iconBW.setPosition(this.width / 2 + 104, height + 48 + 12);
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, int i, String string) {
        context.drawTextWithShadow(textRenderer, getGameTitle() + " " + MTWMod.VERSION, 2, height - 20, 16777215 | i);

        if (mtw_info.getStatus() == ServerInfo.Status.INITIAL) {
            mtw_info.setStatus(ServerInfo.Status.PINGING);
            mtw_info.label = ScreenTexts.EMPTY;
            mtw_info.playerCountLabel = ScreenTexts.EMPTY;
            ServerPingPong.startPinging(client, () -> {
                mtw_info.setStatus(mtw_info.protocolVersion == SharedConstants.getGameVersion().getProtocolVersion() ? ServerInfo.Status.SUCCESSFUL : ServerInfo.Status.INCOMPATIBLE);
                client.execute(this::update);
            }, this::update);
        }

        if (mtw_info.getStatus() == ServerInfo.Status.PINGING) {
            int level = (int) (Util.getMeasuringTimeMs() / 100L & 7L);
            if (level > 4) {
                level = 8 - level;
            }

            statusIconTexture = switch (level) {
                case 1 -> PINGING_2_TEXTURE;
                case 2 -> PINGING_3_TEXTURE;
                case 3 -> PINGING_4_TEXTURE;
                case 4 -> PINGING_5_TEXTURE;
                default -> PINGING_1_TEXTURE;
            };
        }

        int width = this.width / 2 + 104; // playButton width ended
        int height = (this.height / 4 + (48 + 12)) - 22;

        if (statusIconTexture != null) {
            context.drawGuiTexture(statusIconTexture, width - 15, height, 10, 8);
        }
        var text = mtw_info.getStatus() == ServerInfo.Status.INCOMPATIBLE
                ? mtw_info.version.copy().formatted(Formatting.RED)
                : mtw_info.playerCountLabel;
        int j = textRenderer.getWidth(text);
        context.drawText(textRenderer, text, width - j - 15 - 2, height + 1, -8355712, false);

        var m = mouseY - height;
        if (statusTooltipText != null && mouseX >= width - 15 && mouseX <= width - 5 && m >= 0 && m <= 8) {
            context.drawTooltip(textRenderer, List.of(statusTooltipText), mouseX, mouseY);
        } else if (playerListSummary != null && mouseX >= width - j - 15 - 2 && mouseX <= width - 15 - 2 && m >= 0 && m <= 8) {
            context.drawTooltip(textRenderer, playerListSummary, mouseX, mouseY);
        }
    }

    @Unique
    private void update() {
        playerListSummary = null;
        switch (mtw_info.getStatus()) {
            case INITIAL:
            case PINGING:
                statusIconTexture = PING_1_TEXTURE;
                statusTooltipText = PINGING_TEXT;
                break;
            case INCOMPATIBLE:
                statusIconTexture = INCOMPATIBLE_TEXTURE;
                statusTooltipText = INCOMPATIBLE_TEXT;
                playerListSummary = mtw_info.playerListSummary;
                break;
            case UNREACHABLE:
                statusIconTexture = UNREACHABLE_TEXTURE;
                statusTooltipText = NO_CONNECTION_TEXT;
                break;
            case SUCCESSFUL:
                if (mtw_info.ping < 150L) {
                    statusIconTexture = PING_5_TEXTURE;
                } else if (mtw_info.ping < 300L) {
                    statusIconTexture = PING_4_TEXTURE;
                } else if (mtw_info.ping < 600L) {
                    statusIconTexture = PING_3_TEXTURE;
                } else if (mtw_info.ping < 1000L) {
                    statusIconTexture = PING_2_TEXTURE;
                } else {
                    statusIconTexture = PING_1_TEXTURE;
                }

                statusTooltipText = Text.translatable("multiplayer.status.ping", mtw_info.ping);
                playerListSummary = mtw_info.playerListSummary;
                break;
        }
    }

    @Unique
    private String getGameTitle() {
        if (I18n.hasTranslation("game.title")) {
            return I18n.translate("game.title");
        }
        return "Cheonnyeon Story";
    }
}
