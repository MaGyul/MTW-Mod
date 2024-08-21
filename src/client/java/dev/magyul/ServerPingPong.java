package dev.magyul;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.magyul.util.ClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import static dev.magyul.util.ClientUtil.mtw_info;

public class ServerPingPong {
    public static boolean serverJoined = false;
    public static Runnable joinStart;
    public static Runnable tick;
    private static boolean isPinging = false;
    private static int tickC = 0;
    private static int repingCount = 0;

    private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(MTWMod.LOGGER)).build());

    public static void joinStart() {
        if (joinStart != null) {
            try {
                joinStart.run();
                joinStart = null;
            } catch (Throwable thr) {
                MTWMod.LOGGER.error("Ping cancel failed", thr);
            }
        }
    }

    public static void tick() {
        if (tick != null) tick.run();
        if (tickC < 100) {
            tickC++;
        } else {
            _reping();
            tickC = 0;
        }
    }

    private static void _reping() {
        if (serverJoined) {
            repingCount = 5;
            return;
        } else {
            if (mtw_info.ping >= 0) {
                if (repingCount < 5) {
                    repingCount++;
                    return;
                } else {
                    repingCount = 0;
                }
            }
        }
        mtw_info.setStatus(ServerInfo.Status.INITIAL);
    }

    public static void startPinging(MinecraftClient client, Runnable pingCallback, Runnable update) {
        if (isPinging) return;
        if (serverJoined) return;
        SERVER_PINGER_THREAD_POOL.submit(() -> {
            try {
                repingCount = 0;
                final var address = ClientUtil.mtw_address;
                var optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);
                if (optional.isEmpty()) {
                    MTWMod.LOGGER.error("Can't ping: {}", ConnectScreen.UNKNOWN_HOST_TEXT.getString());
                    mtw_info.label = Text.translatable("multiplayer.status.cannot_connect").withColor(-65536);
                    mtw_info.playerCountLabel = ScreenTexts.EMPTY;
                } else {
                    isPinging = true;
                    final var isa = optional.get();
                    final var connection = ClientConnection.connect(isa, false, (MultiValueDebugSampleLogImpl) null);
                    mtw_info.label = Text.translatable("multiplayer.status.pinging");
                    mtw_info.playerListSummary = List.of();
                    joinStart = () -> {
                        if (connection.isOpen()) {
                            connection.disconnect(Text.translatable("multiplayer.status.cancelled"));
                            isPinging = false;
                        }
                    };
                    tick = () -> {
                        if (connection.isOpen()) {
                            connection.tick();
                        } else {
                            tick = null;
                            connection.handleDisconnection();
                        }
                    };
                    var listener = new CQPListener(connection, pingCallback);
                    try {
                        connection.connect(address.getAddress(), address.getPort(), listener);
                        connection.send(QueryRequestC2SPacket.INSTANCE);
                    } catch (Throwable thr) {
                        MTWMod.LOGGER.error("Failed to ping mtw server", thr);
                    }
                }
            } catch (Exception ex) {
                isPinging = false;
                //noinspection ConstantValue
                if (ex instanceof UnknownHostException) {
                    mtw_info.setStatus(ServerInfo.Status.UNREACHABLE);
                    mtw_info.label = Text.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
                    client.execute(update);
                } else {
                    mtw_info.setStatus(ServerInfo.Status.UNREACHABLE);
                    mtw_info.label = Text.translatable("multiplayer.status.cannot_connect").withColor(-65536);
                    client.execute(update);
                }
            }
        });
    }


    private static class CQPListener implements ClientQueryPacketListener {
        private final ClientConnection connection;
        private final Runnable pingCallback;
        private boolean sentQuery;
        private boolean received;
        private long startTime;

        private CQPListener(ClientConnection connection, Runnable pingCallback) {
            this.connection = connection;
            this.pingCallback = pingCallback;
        }

        @Override
        public void onResponse(QueryResponseS2CPacket packet) {
            if (received) {
                connection.disconnect(Text.translatable("multiplayer.status.unrequested"));
                isPinging = false;
            } else {
                received = true;
                var metadata = packet.metadata();
                mtw_info.label = metadata.description();
                metadata.version().ifPresentOrElse((version) -> {
                    mtw_info.version = Text.literal(version.gameVersion());
                    mtw_info.protocolVersion  = version.protocolVersion();
                }, () -> {
                    mtw_info.version = Text.translatable("multiplayer.status.old");
                    mtw_info.protocolVersion = 0;
                });
                metadata.players().ifPresentOrElse((players) -> {
                    mtw_info.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(players.online(), players.max());
                    mtw_info.players = players;
                    if (!players.sample().isEmpty()) {
                        List<Text> list = new ArrayList<>(players.sample().size());

                        for (var profile : players.sample()) {
                            list.add(Text.literal(profile.getName()));
                        }

                        if (players.sample().size() < players.online()) {
                            list.add(Text.translatable("multiplayer.status.and_more", players.online() - players.sample().size()));
                        }

                        mtw_info.playerListSummary = list;
                    } else {
                        mtw_info.playerListSummary = List.of();
                    }
                }, () -> mtw_info.playerCountLabel = Text.translatable("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY));
                metadata.favicon().ifPresent((favicon) -> {
                    if (!Arrays.equals(favicon.iconBytes(), mtw_info.getFavicon())) {
                        mtw_info.setFavicon(ServerInfo.validateFavicon(favicon.iconBytes()));
                    }
                });
                startTime = Util.getMeasuringTimeMs();
                connection.send(new QueryPingC2SPacket(startTime));
                sentQuery = true;
            }
        }

        @Override
        public void onPingResult(PingResultS2CPacket packet) {
            mtw_info.ping = Util.getMeasuringTimeMs() - startTime;
            connection.disconnect(Text.translatable("multiplayer.status.finished"));
            pingCallback.run();
            isPinging = false;
        }

        @Override
        public void onDisconnected(DisconnectionInfo info) {
            isPinging = false;
            if (!sentQuery) {
                MTWMod.LOGGER.error("Can't ping: {}", info.reason().getString());
                mtw_info.label = Text.translatable("multiplayer.status.cannot_connect").withColor(-65536);
                mtw_info.playerCountLabel = ScreenTexts.EMPTY;
            }
        }

        @Override
        public boolean isConnectionOpen() {
            return connection.isOpen();
        }
    }
}
