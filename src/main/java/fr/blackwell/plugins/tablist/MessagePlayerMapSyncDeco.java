package fr.blackwell.plugins.tablist;

import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerMapSyncDeco implements IMessage {
    private String username;

    public MessagePlayerMapSyncDeco() {
    }

    public MessagePlayerMapSyncDeco(String username) {

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

    public static class Handler implements IMessageHandler<MessagePlayerMapSyncDeco, IMessage> {

        @Override
        public IMessage onMessage(MessagePlayerMapSyncDeco message, MessageContext ctx) {

            //Client Side
            BWPlayerProfileManagement.PLAYER_MAP.remove(message.username);

            return null;
        }
    }
}
