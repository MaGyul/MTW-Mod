package dev.magyul.screen;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import dev.magyul.MTWModClient;
import dev.magyul.screen.overlay.ConfirmLinkOverlayScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("DataFlowIssue")
@Environment(EnvType.CLIENT)
public abstract class OverlayScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
    @Nullable
    private URI clickedLink;

    protected OverlayScreen(Text title) {
        super(title);
    }

    @Override
    public void close() {
        if (MTWModClient.instance.overlayScreen == null) {
            client.setScreen(null);
        } else {
            MTWModClient.instance.setOverlayScreen(null);
        }
    }

    public boolean handleTextClick(@Nullable Style style) {
        if (style != null) {
            ClickEvent clickEvent = style.getClickEvent();
            if (hasShiftDown()) {
                if (style.getInsertion() != null) {
                    this.insertText(style.getInsertion(), false);
                }
            } else if (clickEvent != null) {
                URI uRI;
                if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.client.options.getChatLinks().getValue()) {
                        return false;
                    }

                    try {
                        uRI = new URI(clickEvent.getValue());
                        String string = uRI.getScheme();
                        if (string == null) {
                            throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
                        }

                        if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
                        }

                        if (this.client.options.getChatLinksPrompt().getValue()) {
                            this.clickedLink = uRI;
                            MTWModClient.instance.setOverlayScreen(
                                    new ConfirmLinkOverlayScreen(this::confirmLink, clickEvent.getValue(), false)
                            );
                        } else {
                            this.openLink(uRI);
                        }
                    } catch (URISyntaxException var5) {
                        LOGGER.error("Can't open url for {}", clickEvent, var5);
                    }
                } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    uRI = (new File(clickEvent.getValue())).toURI();
                    this.openLink(uRI);
                } else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(SharedConstants.stripInvalidChars(clickEvent.getValue()), true);
                } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    String string2 = SharedConstants.stripInvalidChars(clickEvent.getValue());
                    if (string2.startsWith("/")) {
                        if (!this.client.player.networkHandler.sendCommand(string2.substring(1))) {
                            LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", string2);
                        }
                    } else {
                        LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", string2);
                    }
                } else if (clickEvent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                    this.client.keyboard.setClipboard(clickEvent.getValue());
                } else {
                    LOGGER.error("Don't know how to handle {}", clickEvent);
                }

                return true;
            }

        }
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
    }

    private void confirmLink(boolean open) {
        if (open) {
            this.openLink(this.clickedLink);
        }

        this.clickedLink = null;
        MTWModClient.instance.setOverlayScreen(this);
    }

    private void openLink(URI link) {
        Util.getOperatingSystem().open(link);
    }

}
