package de.ellpeck.naturesaura.packet;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketParticleStream implements IMessage {

    private float startX;
    private float startY;
    private float startZ;

    private float endX;
    private float endY;
    private float endZ;

    private float speed;
    private int color;
    private float scale;

    public PacketParticleStream(float startX, float startY, float startZ, float endX, float endY, float endZ, float speed, int color, float scale) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
        this.speed = speed;
        this.color = color;
        this.scale = scale;
    }

    public PacketParticleStream() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.startX = buf.readFloat();
        this.startY = buf.readFloat();
        this.startZ = buf.readFloat();
        this.endX = buf.readFloat();
        this.endY = buf.readFloat();
        this.endZ = buf.readFloat();
        this.speed = buf.readFloat();
        this.color = buf.readInt();
        this.scale = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.startX);
        buf.writeFloat(this.startY);
        buf.writeFloat(this.startZ);
        buf.writeFloat(this.endX);
        buf.writeFloat(this.endY);
        buf.writeFloat(this.endZ);
        buf.writeFloat(this.speed);
        buf.writeInt(this.color);
        buf.writeFloat(this.scale);
    }

    public static class Handler implements IMessageHandler<PacketParticleStream, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketParticleStream message, MessageContext ctx) {
            NaturesAura.proxy.scheduleTask(() -> NaturesAuraAPI.instance().spawnParticleStream(
                    message.startX, message.startY, message.startZ,
                    message.endX, message.endY, message.endZ,
                    message.speed, message.color, message.scale));

            return null;
        }
    }
}