package dev.magyul.network;

import dev.magyul.blocks.entities.SignBoardEntity;
import dev.magyul.util.ClientUtil;
import dev.magyul.util.ServerUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class CNetwork {

    public static void register() {
        // Use Voice Activity
        ClientPlayNetworking.registerGlobalReceiver(UpdateAllMicS2CPacket.TYPE, CNetwork::receivedUpdateAllMic);
        ClientPlayNetworking.registerGlobalReceiver(SignBoardEditorOpenS2CPacket.TYPE, CNetwork::receivedSignBoardEditorOpen);
    }

    private static void receivedSignBoardEditorOpen(SignBoardEditorOpenS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        var world = player.getWorld();
        BlockPos blockPos = packet.pos();
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof SignBoardEntity signBlockEntity) {
            ServerUtil.clientCallSignBoardEntity.accept(signBlockEntity);
        } else {
            BlockState blockState = world.getBlockState(blockPos);
            SignBoardEntity signBlockEntity = new SignBoardEntity(blockPos, blockState);
            signBlockEntity.setWorld(world);
            ServerUtil.clientCallSignBoardEntity.accept(signBlockEntity);
        }
    }

    private static void receivedUpdateAllMic(UpdateAllMicS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ClientUtil.setAllMic(packet.value());
    }
}
