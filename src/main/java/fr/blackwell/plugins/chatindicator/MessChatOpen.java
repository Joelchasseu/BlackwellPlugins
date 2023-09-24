package fr.blackwell.plugins.chatindicator;

import fr.blackwell.plugins.tablist.BWPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessChatOpen implements IMessage {

    private String username;
    @Override
    public void fromBytes(ByteBuf buf) {

        this.username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {

        ByteBufUtils.writeUTF8String(buf, Minecraft.getMinecraft().player.getName());
    }

    public static class Handler implements IMessageHandler<MessChatOpen, IMessage> {

        @Override
        public IMessage onMessage(MessChatOpen message, MessageContext ctx) {

            BWPacketHandler.INSTANCE.sendToAll(new MessChatOpenAns(message.username));
            return null;
        }
    }
}
