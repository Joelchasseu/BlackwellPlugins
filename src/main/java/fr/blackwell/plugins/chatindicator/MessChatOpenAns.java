package fr.blackwell.plugins.chatindicator;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessChatOpenAns implements IMessage {


    private String username;

    public MessChatOpenAns() {
    }

    public MessChatOpenAns(String username) {
        this.username = username;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

        this.username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {

        ByteBufUtils.writeUTF8String(buf, this.username);
    }

    public static class Handler implements IMessageHandler<MessChatOpenAns, IMessage> {

        @Override
        public IMessage onMessage(MessChatOpenAns message, MessageContext ctx) {
            if (!ChatEvent.CHATTING_PLAYER_LIST.contains(message.username))
                ChatEvent.CHATTING_PLAYER_LIST.add(message.username);
            return null;
        }
    }
}
