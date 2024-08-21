package dev.magyul.network;

import dev.magyul.api.LogicalSide;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface IPacket extends CustomPayload {
    NbtCompound EMPTY = new NbtCompound();

    void write(RegistryByteBuf buf);

    void handle(Context context);

    PacketType<?> getType();

    default Id<? extends CustomPayload> getId() {
        return getType().getId();
    }

    static ItemStack readItemStack(RegistryByteBuf buf) {
        return ItemStack.fromNbtOrEmpty(buf.getRegistryManager(), Objects.requireNonNullElse(buf.readNbt(), EMPTY));
    }

    default void writeItemStack(RegistryByteBuf buf, ItemStack stack) {
        buf.writeNbt(stack.encode(buf.getRegistryManager()));
    }

    class Context {
        private final Object instance;
        private final PlayerEntity player;
        private final PacketSender sender;

        public Context(Object instance, PlayerEntity player, PacketSender sender) {
            this.instance = instance;
            this.player = player;
            this.sender = sender;
        }

        public LogicalSide side() {
            return (player instanceof ServerPlayerEntity) ? LogicalSide.SERVER : LogicalSide.CLIENT;
        }

        @Nullable
        public MinecraftServer server() {
            if (side().isServer()) {
                return (MinecraftServer) instance;
            }
            return null;
        }

        @Nullable
        @Environment(EnvType.CLIENT)
        @SuppressWarnings("unchecked")
        public <T> T client() {
            if (side().isClient()) {
                try {
                    return (T) instance;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        public PlayerEntity player() {
            return player;
        }

        PacketSender responseSender() {
            return sender;
        }

        public void sendPacket(IPacket packet) {
            responseSender().sendPacket(packet);
        }
    }
}
