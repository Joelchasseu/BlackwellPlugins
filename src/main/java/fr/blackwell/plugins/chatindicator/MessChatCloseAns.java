package fr.blackwell.plugins.chatindicator;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessChatCloseAns implements IMessage {

    private String username;
    public MessChatCloseAns() {
    }

    public MessChatCloseAns(String username){
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

    public static class Handler implements IMessageHandler<MessChatCloseAns, IMessage> {

        @Override
        public IMessage onMessage(MessChatCloseAns message, MessageContext ctx) {

            ChatEvent.CHATTING_PLAYER_LIST.remove(message.username);
            return null;
        }
    }
}
