package dev.magyul.mixin.network;

import dev.magyul.MTWMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.QueryableServer;
import net.minecraft.network.handler.LegacyQueries;
import net.minecraft.network.handler.LegacyQueryHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;
import java.util.Locale;

@Mixin(LegacyQueryHandler.class)
public abstract class LegacyQueryHandlerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private QueryableServer server;

    @Inject(method = "channelRead", at = @At("HEAD"), cancellable = true)
    private void channelRead(ChannelHandlerContext ctx, Object msg, CallbackInfo cb) {
        cb.cancel();
        ByteBuf byteBuf = (ByteBuf)msg;
        byteBuf.markReaderIndex();
        boolean bl = true;

        try {
            try {
                if (byteBuf.readUnsignedByte() != 254) {
                    return;
                }

                SocketAddress socketAddress = ctx.channel().remoteAddress();
                int i = byteBuf.readableBytes();
                String string;
                if (i == 0) {
                    LOGGER.debug("Ping: (<1.3.x) from {}", socketAddress);
                    string = getResponseFor1_2();
                    reply(ctx, createBuf(ctx.alloc(), string));
                } else {
                    var isNew = byteBuf.readUnsignedByte();
                    boolean isMTW = false;
                    if (isNew == Byte.MAX_VALUE) {
                        isNew = byteBuf.readUnsignedByte();
                        isMTW = true;
                    }
                    if (isNew != 1) {
                        return;
                    }

                    if (byteBuf.isReadable()) {
                        if (!isLegacyQuery(byteBuf)) {
                            return;
                        }

                        LOGGER.debug("Ping: (1.6) from {}", socketAddress);
                    } else {
                        LOGGER.debug("Ping: (1.4-1.5.x) from {}", socketAddress);
                    }

                    string = getResponse(this.server, isMTW);
                    reply(ctx, createBuf(ctx.alloc(), string));
                }

                byteBuf.release();
                bl = false;
            } catch (RuntimeException e) {
                MTWMod.LOGGER.error("ping error: ", e);
            }
        } finally {
            if (bl) {
                byteBuf.resetReaderIndex();
                ctx.channel().pipeline().remove(This());
                ctx.fireChannelRead(msg);
            }

        }
    }

    @Unique
    private LegacyQueryHandler This() {
        return (LegacyQueryHandler) (Object) this;
    }

    @Unique
    private static boolean isLegacyQuery(ByteBuf buf) {
        short s = buf.readUnsignedByte();
        if (s != 250) {
            return false;
        } else {
            String string = LegacyQueries.read(buf);
            if (!"MC|PingHost".equals(string)) {
                return false;
            } else {
                int i = buf.readUnsignedShort();
                if (buf.readableBytes() != i) {
                    return false;
                } else {
                    short t = buf.readUnsignedByte();
                    if (t < 73) {
                        return false;
                    } else {
                        LegacyQueries.read(buf);
                        int j = buf.readInt();
                        return j <= 65535;
                    }
                }
            }
        }
    }

    @Unique
    private static String getResponseFor1_2() {
        return String.format(Locale.ROOT, "%s§%d§%d",
                "This server can be accessed through a launcher created by Make The World.\n이 서버는 Make The World에서 제작된 런쳐를 통해 접속 할 수 있습니다.",
                0, 0);
    }

    @Unique
    private static String getResponse(QueryableServer server, boolean isMTW) {
        var result = String.format(Locale.ROOT,
                "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                127,
                server.getVersion(),
                server.getServerMotd(),
                server.getCurrentPlayerCount(),
                server.getMaxPlayerCount());
        if (!isMTW) {
            result = String.format(Locale.ROOT,
                    "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                    127, MTWMod.VERSION,
                    "This server can be accessed through a launcher created by Make The World.\n이 서버는 Make The World에서 제작된 런쳐를 통해 접속 할 수 있습니다.",
                    server.getCurrentPlayerCount(), server.getMaxPlayerCount());
        }
        return result;
    }

    @Unique
    private static void reply(ChannelHandlerContext context, ByteBuf buf) {
        context.pipeline().firstContext().writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    }

    @Unique
    private static ByteBuf createBuf(ByteBufAllocator allocator, String string) {
        ByteBuf byteBuf = allocator.buffer();
        byteBuf.writeByte(255);
        LegacyQueries.write(byteBuf, string);
        return byteBuf;
    }
}
