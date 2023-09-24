package fr.blackwell.plugins.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.management.Descriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class ChatProcessing {

    public static final HashMap<String, ChatCanal> CHAT_CANAL_NAME = new HashMap<>();
    public static final HashMap<String, ChatCanal> CHAT_CANAL_PREFIX = new HashMap<>();
    public static final List<ChatCanal> CANAL_LIST = new ArrayList<>();

    @SubscribeEvent
    public static void onChatProcessing(ServerChatEvent event) {

        event.setCanceled(true);



        EntityPlayerMP sender = event.getPlayer();
        String message = event.getMessage();
        String username = sender.getName();


        String prefix;
        ChatCanal canal = null;
        for (int i = 0; i < CHAT_CANAL_PREFIX.size(); i++) {

            prefix = CANAL_LIST.get(i).getPrefix();

            if (message.startsWith(prefix)) {
                canal = CANAL_LIST.get(i);
                message = message.substring(prefix.length());
            }
        }
        if (canal == null)
            canal = CHAT_CANAL_PREFIX.get("default");

        List<EntityPlayerMP> onlinePlayerNames = FMLServerHandler.instance().getServer().getPlayerList().getPlayers();

        String name = BWPlayerProfileManagement.PLAYER_MAP.get(username).getFirstName() + " " + BWPlayerProfileManagement.PLAYER_MAP.get(username).getLastName();
        TextComponentString messageToSend;

        if (canal.getStructureType().equals("emote"))
            messageToSend = new TextComponentString(name + " " + message);
        else if (canal.getStructureType().equals("desc"))
            messageToSend = new TextComponentString(message);
        else
            messageToSend = new TextComponentString(name + " : " + message);

        if (!canal.getChatPrefix().equals(""))
            messageToSend = new TextComponentString("[" + canal.getChatPrefix() + "] " + messageToSend.getText());

        messageToSend.setStyle(canal.getStyle());

        BWPlayer player;
        boolean feedback = false;
        for (int i = 0; i < onlinePlayerNames.size(); i++) {

            if (canal.getRange() != 0 && !canal.getStructureType().equals("staff")) {
                if (sender.getPosition().distanceSq(onlinePlayerNames.get(i).getPosition()) < canal.getRange() * canal.getRange())
                    onlinePlayerNames.get(i).sendMessage(messageToSend);

            } else if (canal.getStructureType().equals("staff") && !canal.getName().equals("staff")) {
                if (!BWPlayerProfileManagement.PLAYER_MAP.get(username).isStaff() && !feedback) {
                    sender.sendMessage(messageToSend);
                    feedback = true;
                }

                player = BWPlayerProfileManagement.PLAYER_MAP.get(onlinePlayerNames.get(i).getName());

                if (player.isStaff()) {
                    onlinePlayerNames.get(i).sendMessage(messageToSend);
                }

            } else if (canal.getStructureType().equals("staff") && canal.getName().equals("staff")) {

                if (BWPlayerProfileManagement.PLAYER_MAP.get(username).isStaff()) {
                    player = BWPlayerProfileManagement.PLAYER_MAP.get(onlinePlayerNames.get(i).getName());
                    if (player.isStaff())
                        onlinePlayerNames.get(i).sendMessage(messageToSend);
                } else
                    sender.sendMessage(new TextComponentString("Vous n'avez pas la permission d'utiliser ce canal").setStyle(new Style().setColor(TextFormatting.RED)));

            } else onlinePlayerNames.get(i).sendMessage(messageToSend);
        }
    }

    public static void loadChatCanals() {

        JsonArray array = BWJSONUtils.getJsonArrayFromFile(BlackwellPlugins.CHAT_CONFIG_FILE);

        JsonObject canal;
        for (int i = 0; i < array.size(); i++) {
            canal = array.get(i).getAsJsonObject();

            if (canal.get("prefix").getAsString().equals(""))
                canal.addProperty("prefix", "default");

            ChatCanal chatCanal = new ChatCanal(canal);
            BlackwellPlugins.logger.info("Le canal " + chatCanal.getName() + " a bien été chargé");
            CHAT_CANAL_PREFIX.put(canal.get("prefix").getAsString(), chatCanal);
            CHAT_CANAL_NAME.put(canal.get("name").getAsString(), chatCanal);
            CANAL_LIST.add(chatCanal);
        }
    }

    public static void createChatConfigFile() {
        if (!BlackwellPlugins.CHAT_CONFIG_FILE.exists()) {
            BWJSONUtils.createNewJson(BlackwellPlugins.CHAT_CONFIG_FILE);

            JsonArray array = new JsonArray();

            JsonObject speak = new JsonObject();
            speak.addProperty("name", "speak");
            speak.addProperty("prefix", "");
            speak.addProperty("range", 10);
            speak.addProperty("color", 15);
            speak.addProperty("italic", false);
            speak.addProperty("bold", false);
            speak.addProperty("structure", "speak");
            speak.addProperty("chatPrefix", "");
            array.add(speak);

            JsonObject whisper = new JsonObject();
            whisper.addProperty("name", "whisper");
            whisper.addProperty("prefix", "~");
            whisper.addProperty("range", 2);
            whisper.addProperty("color", 13);
            whisper.addProperty("italic", true);
            whisper.addProperty("bold", false);
            whisper.addProperty("structure", "speak");
            whisper.addProperty("chatPrefix", "w");
            array.add(whisper);

            JsonObject emote = new JsonObject();
            emote.addProperty("name", "emote");
            emote.addProperty("prefix", "*");
            emote.addProperty("range", 10);
            emote.addProperty("color", 10);
            emote.addProperty("italic", false);
            emote.addProperty("bold", false);
            emote.addProperty("structure", "emote");
            emote.addProperty("chatPrefix", "");
            array.add(emote);

            JsonObject description = new JsonObject();
            description.addProperty("name", "desc");
            description.addProperty("prefix", "$");
            description.addProperty("range", 10);
            description.addProperty("color", 7);
            description.addProperty("italic", false);
            description.addProperty("bold", false);
            description.addProperty("structure", "desc");
            description.addProperty("chatPrefix", "");
            array.add(description);

            JsonObject staff = new JsonObject();
            staff.addProperty("name", "staff");
            staff.addProperty("prefix", "=");
            staff.addProperty("range", 0);
            staff.addProperty("color", 4);
            staff.addProperty("italic", false);
            staff.addProperty("bold", false);
            staff.addProperty("structure", "staff");
            staff.addProperty("chatPrefix", "Staff");
            array.add(staff);

            JsonObject hrp = new JsonObject();
            hrp.addProperty("name", "hrp");
            hrp.addProperty("prefix", "&");
            hrp.addProperty("range", 0);
            hrp.addProperty("color", 7);
            hrp.addProperty("italic", false);
            hrp.addProperty("bold", false);
            hrp.addProperty("structure", "speak");
            hrp.addProperty("chatPrefix", "HRP");
            array.add(hrp);

            JsonObject help = new JsonObject();
            help.addProperty("name", "help");
            help.addProperty("prefix", "?");
            help.addProperty("range", 0);
            help.addProperty("color", 7);
            help.addProperty("italic", false);
            help.addProperty("bold", false);
            help.addProperty("structure", "staff");
            help.addProperty("chatPrefix", "Help");
            array.add(help);

            JsonObject shout = new JsonObject();
            shout.addProperty("name", "shout");
            shout.addProperty("prefix", "!");
            shout.addProperty("range", 20);
            shout.addProperty("color", 12);
            shout.addProperty("italic", false);
            shout.addProperty("bold", true);
            shout.addProperty("structure", "speak");
            shout.addProperty("chatPrefix", "");
            array.add(shout);

            try (FileWriter fileWriter = new FileWriter(BlackwellPlugins.CHAT_CONFIG_FILE)) {
                //We can write any JSONArray or JSONObject instance to the file
                fileWriter.write(array.toString());
                fileWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
