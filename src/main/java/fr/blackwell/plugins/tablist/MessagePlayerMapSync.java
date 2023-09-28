package fr.blackwell.plugins.tablist;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWJSONUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerMapSync implements IMessage {


    private String username;
    JsonObject profile;
    public MessagePlayerMapSync(){

    }

    public MessagePlayerMapSync(String username){
        this.username = username;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

        JsonParser parser = new JsonParser();
        this.profile = parser.parse(ByteBufUtils.readUTF8String(buf)).getAsJsonObject();
    }

    @Override
    public void toBytes(ByteBuf buf) {

        JsonObject profile = BWPlayerProfileManagement.PLAYER_MAP.get(this.username).getProfile();
        ByteBufUtils.writeUTF8String(buf, profile.toString());
    }

    public static class Handler implements IMessageHandler<MessagePlayerMapSync, MessagePlayerMapSyncConnection> {

        @Override
        public MessagePlayerMapSyncConnection onMessage(MessagePlayerMapSync message, MessageContext ctx) {

            BWPlayerProfileManagement.loadPlayerDataToClient(message.profile, message.profile.get("username").getAsString());

            return null;
        }
    }
}
