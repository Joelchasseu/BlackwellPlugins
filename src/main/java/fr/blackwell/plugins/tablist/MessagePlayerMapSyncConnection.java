package fr.blackwell.plugins.tablist;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWJSONUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.HashMap;
import java.util.Set;


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

        HashMap<String, BWPlayer> playerMap = BWPlayerProfileManagement.PLAYER_MAP;
        String[] keySet = playerMap.keySet().toArray(new String[0]);


        for (int i = 0; i < playerMap.size(); i++) {
            if (playerMap.containsKey(keySet[i]))
                array.add(playerMap.get(keySet[i]).getProfile());
        }
        ByteBufUtils.writeUTF8String(buf, array.toString());
    }

    public static class Handler implements IMessageHandler<MessagePlayerMapSyncConnection, IMessage> {

        @Override
        public IMessage onMessage(MessagePlayerMapSyncConnection message, MessageContext ctx) {

            BWPlayerProfileManagement.PLAYER_MAP.clear();

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
