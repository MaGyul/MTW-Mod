package dev.magyul.network;

import dev.magyul.MTWMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SignBoardEditorOpenS2CPacket(BlockPos pos) implements FabricPacket {
    public static final PacketType<SignBoardEditorOpenS2CPacket> TYPE = PacketType.create(new Identifier(MTWMod.ID, "sign_board_editor_open"), SignBoardEditorOpenS2CPacket::new);

    public SignBoardEditorOpenS2CPacket(PacketByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
