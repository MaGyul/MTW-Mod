package dev.magyul.voicechat;

import de.maxhenkel.voicechat.voice.common.SoundPacket;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class AllSoundPacket extends SoundPacket<AllSoundPacket> {

    private String senderName;

    public AllSoundPacket(UUID sender, byte[] data, long sequenceNumber, String senderName) {
        super(sender, sender, data, sequenceNumber, null);
        this.senderName = senderName;
    }

    public AllSoundPacket() {
    }

    public String getSenderName() {
        return senderName;
    }

    public AllSoundPacket fromBytes(PacketByteBuf buf) {
        AllSoundPacket soundPacket = new AllSoundPacket();
        soundPacket.channelId = buf.readUuid();
        soundPacket.sender = buf.readUuid();
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();
        byte data = buf.readByte();
        if (this.hasFlag(data, (byte)2)) {
            soundPacket.category = buf.readString(16);
        }
        soundPacket.senderName = buf.readString();

        return soundPacket;
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeUuid(this.channelId);
        buf.writeUuid(this.sender);
        buf.writeByteArray(this.data);
        buf.writeLong(this.sequenceNumber);
        byte data = 0;
        if (this.category != null) {
            data = this.setFlag(data, (byte)2);
        }

        buf.writeByte(data);
        if (this.category != null) {
            buf.writeString(this.category, 16);
        }

        buf.writeString(senderName);
    }
}
