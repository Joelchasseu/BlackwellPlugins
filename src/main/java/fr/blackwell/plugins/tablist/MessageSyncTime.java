package fr.blackwell.plugins.tablist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncTime implements IMessage {

    JsonObject timeData;
    @Override
    public void fromBytes(ByteBuf buf) {
        JsonParser parser = new JsonParser();
        timeData = parser.parse(ByteBufUtils.readUTF8String(buf)).getAsJsonObject();
    }

    @Override
    public void toBytes(ByteBuf buf) {

        ByteBufUtils.writeUTF8String(buf, TimeManagement.timeData.toString());
    }

    public static class Handler implements IMessageHandler<MessageSyncTime, IMessage> {

        @Override
        public IMessage onMessage(MessageSyncTime message, MessageContext ctx) {

            TimeManagement.timeData = message.timeData;
            return null;
        }
    }
}
