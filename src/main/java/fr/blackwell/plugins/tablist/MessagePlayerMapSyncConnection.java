package fr.blackwell.plugins.tablist;

import com.google.gson.JsonArray;
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
import net.minecraftforge.fml.server.FMLServerHandler;


public class MessagePlayerMapSyncConnection implements IMessage {

    private JsonArray arrayPlayers;

    public MessagePlayerMapSyncConnection() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

        JsonParser parser = new JsonParser();
        this.arrayPlayers = parser.parse(ByteBufUtils.readUTF8String(buf)).getAsJsonArray();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        JsonArray array = new JsonArray();
        String[] onlinePlayers = FMLServerHandler.instance().getServer().getOnlinePlayerNames();

        JsonObject indexFile = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
        for (int i = 0; i < onlinePlayers.length; i++) {
            if (indexFile.has(onlinePlayers[i]))
                array.add(indexFile.get(onlinePlayers[i]));
        }
        ByteBufUtils.writeUTF8String(buf, array.toString());
    }

    public static class Handler implements IMessageHandler<MessagePlayerMapSyncConnection, IMessage> {

        @Override
        public IMessage onMessage(MessagePlayerMapSyncConnection message, MessageContext ctx) {

            JsonObject profile;
            JsonArray index = message.arrayPlayers;
            for (int i = 0; i < index.size(); i ++){
                profile = index.get(i).getAsJsonObject();
                BWPlayerProfileManagement.loadPlayerDataToClient(profile,profile.get("username").getAsString());
            }

            //Client Side


            return null;
        }
    }
}
