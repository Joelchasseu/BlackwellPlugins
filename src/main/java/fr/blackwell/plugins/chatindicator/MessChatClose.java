package fr.blackwell.plugins.chatindicator;

import fr.blackwell.plugins.tablist.BWPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessChatClose implements IMessage {

    private String username;
    @Override
    public void fromBytes(ByteBuf buf) {
        this.username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, Minecraft.getMinecraft().player.getName());
    }

    public static class Handler implements IMessageHandler<MessChatClose, IMessage> {

        @Override
        public IMessage onMessage(MessChatClose message, MessageContext ctx) {
            System.out.println(ctx.getServerHandler().player.getName());
            BWPacketHandler.INSTANCE.sendToAll(new MessChatCloseAns(message.username));

            return null;
        }
    }
}
