package dev.magyul.util;

import com.mojang.logging.LogUtils;
import dev.magyul.ServerPingPong;
import io.netty.channel.ChannelFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.state.LoginStates;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.magyul.util.ClientUtil.playStatus;

public class ConnectServer {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final long NARRATION_DELAY_MS = 2000L;
    public static final Text ABORT_CONNECTION = Text.translatable("connect.aborted");
    public static final Text UNKNOWN_HOST_MESSAGE = Text.translatable("disconnect.genericReason", Text.translatable("disconnect.unknownHost"));
    @Nullable
    volatile ClientConnection connection;
    @Nullable
    ChannelFuture channelFuture;
    public volatile boolean cancel;
    private long lastNarration = -1L;
    final Text connectFailedTitle;
    final MinecraftClient minecraft;

    private ConnectServer(MinecraftClient mc, Text text) {
        this.minecraft = mc;
        this.connectFailedTitle = text;
    }

    public static ConnectServer startConnecting(MinecraftClient mc, ServerAddress address, ServerInfo info) {
        ServerPingPong.joinStart();
        ServerPingPong.serverJoined = true;
        ConnectServer cs = new ConnectServer(mc, ScreenTexts.CONNECT_FAILED);
        mc.disconnect(new TitleScreen());
        mc.loadBlockList();
        mc.ensureAbuseReportContext(ReporterEnvironment.ofThirdPartyServer(info.address));
        mc.getQuickPlayLogger().setWorld(QuickPlayLogger.WorldType.MULTIPLAYER, info.address, info.name);
        cs.connect(mc, address, info);

        return cs;
    }

    private void connect(final MinecraftClient client, final ServerAddress address, final ServerInfo info) {
        playStatus = Text.translatable("connect.connecting");
        LOGGER.info("Connecting to {}, {}", address.getAddress(), address.getPort());
        Thread thread = new Thread("Server Connector #"  + UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                InetSocketAddress isa = null;

                try {
                    if (cancel) return;

                    Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);
                    if (cancel) return;

                    if (optional.isEmpty()) {
                        LOGGER.error("Couldn't connect to server: Unknown host \"{}\"", address.getAddress());
                        client.execute(() ->
                                client.setScreen(new DisconnectedScreen(new TitleScreen(), connectFailedTitle, UNKNOWN_HOST_MESSAGE)));
                        return;
                    }

                    isa = optional.get();
                    ClientConnection connection;
                    synchronized (this) {
                        if (cancel) return;

                        connection = new ClientConnection(NetworkSide.CLIENTBOUND);
                        connection.resetPacketSizeLog(client.getDebugHud().getPacketSizeLog());
                        channelFuture = ClientConnection.connect(isa, client.options.shouldUseNativeTransport(), connection);
                    }

                    channelFuture.syncUninterruptibly();
                    synchronized (this) {
                        if (cancel) {
                            connection.disconnect(ABORT_CONNECTION);
                            return;
                        }

                        ConnectServer.this.connection = connection;
                        client.getServerResourcePackProvider().init(connection, convertPackStatus(info.getResourcePackPolicy()));
                    }

                    connection.connect(isa.getHostName(), isa.getPort(), LoginStates.C2S, LoginStates.S2C, new ClientLoginNetworkHandler(connection, client, info, new TitleScreen(), false, null, ConnectServer.this::updateStatus, null), false);
                    connection.send(new LoginHelloC2SPacket(client.getSession().getUsername(), client.getSession().getUuidOrNull()));
                } catch (Exception ex) {
                    if (cancel) return;

                    Throwable throwable = ex.getCause();
                    Exception exp;
                    if (throwable instanceof Exception exp1) {
                        exp = exp1;
                    } else {
                        exp = ex;
                    }

                    LOGGER.error("Couldn't connect to server", ex);
                    String str = isa == null ? exp.getMessage() : exp.getMessage().replaceAll(isa.getHostName() + ":" + isa.getPort(), "").replaceAll(isa.toString(), "");
                    client.execute(() ->
                            client.setScreen(new DisconnectedScreen(new TitleScreen(), connectFailedTitle, Text.translatable("disconnect.genericReason", str))));
                }
            }

            private static ServerResourcePackManager.AcceptanceStatus convertPackStatus(ServerInfo.ResourcePackPolicy policy) {
                return switch (policy) {
                    case ENABLED -> ServerResourcePackManager.AcceptanceStatus.ALLOWED;
                    case DISABLED -> ServerResourcePackManager.AcceptanceStatus.DECLINED;
                    case PROMPT -> ServerResourcePackManager.AcceptanceStatus.PENDING;
                };
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    private void updateStatus(Text text) {
        playStatus = text;
    }

    @SuppressWarnings("DataFlowIssue")
    public void tick() {
        if (connection != null) {
            if (connection.isOpen()) {
                connection.tick();
            } else {
                connection.handleDisconnection();
            }
        }
    }

    public void render() {
        long i = Util.getMeasuringTimeMs();
        if (i - lastNarration > NARRATION_DELAY_MS) {
            lastNarration = i;
            minecraft.getNarratorManager().narrate(Text.translatable("narrator.joining"));
        }
    }
}
