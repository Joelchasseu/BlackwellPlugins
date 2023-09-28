package fr.blackwell.plugins.events;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.tablist.*;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Mod.EventBusSubscriber
public class EventsPerm {


    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public static void onPlayerConnection(PlayerEvent.PlayerLoggedInEvent event) {

        //Détermine le pseudo du joueur, si mode Dev pioche dans une liste de pseudos autrement agit normalement
        String username = event.player.getName();
        // ------------------------------------------------------------------------------------------------------

        boolean hasProfile = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE).has(username);

        if (hasProfile) {

            BWPlayerProfileManagement.loadPlayerData(username);
            BlackwellPlugins.logger.info("Le profil du joueur " + username + " a bien été chargé");

        } else {
            JsonObject profile = BWJSONUtils.getPlayerBlackWellDataFromWebsite(username);
            profile.addProperty("uuid", event.player.getGameProfile().getId().toString());
            profile.addProperty("role", "Non Assigné");
            profile.addProperty("type", "Non Assigné");
            profile.addProperty("staff", false);
            if (profile.has("firstname") && !profile.get("firstname").getAsString().equals("")) {
                BWPlayerProfileManagement.writePlayerBWProfile(profile, username);
                BlackwellPlugins.logger.info("Le profil du joueur " + username + " a bien été crée");
                BWPlayerProfileManagement.loadPlayerData(username);
            } else {

                profile.addProperty("model", "default");
                profile.addProperty("firstname", "Visiteur");
                profile.addProperty("lastname", "Sans Profil");
                profile.addProperty("age", "21");
                profile.addProperty("height", "186");
                profile.addProperty("twitter", "");
                profile.addProperty("instagram", "");
                profile.addProperty("role", "Non Assigné");
                profile.addProperty("staff", false);

                BWPlayerProfileManagement.writePlayerBWProfile(profile, username);
                BlackwellPlugins.logger.info("Le profil du joueur " + username + " a bien été crée");
                BWPlayerProfileManagement.loadPlayerData(username);
            }
        }

        //Téléporte les joueurs au lobby à la connection
        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.WARP_FILE);
        if (index.has("lobby")) {
            JsonObject lobbyCoords = index.get("lobby").getAsJsonObject();
            if (lobbyCoords.has("x") && lobbyCoords.has("y") && lobbyCoords.has("z")) {

                int x = lobbyCoords.get("x").getAsInt();
                int y = lobbyCoords.get("y").getAsInt();
                int z = lobbyCoords.get("z").getAsInt();
                event.player.getServer().getPlayerList().getPlayerByUsername(event.player.getName()).setPositionAndUpdate(x, y, z);
            }
        }

        //Synchro du temps côté client
        BWPacketHandler.INSTANCE.sendTo(new MessageSyncTime(), (EntityPlayerMP) event.player);

        //Système de Synchro de la PLAYER_MAP côté client
        BWPacketHandler.INSTANCE.sendTo(new MessagePlayerMapSyncConnection(), (EntityPlayerMP) event.player);

        //Packet pour synchro un nouveau joueur qui se connecte
        List<EntityPlayerMP> onlinePlayers = FMLServerHandler.instance().getServer().getPlayerList().getPlayers();

        for (int i = 0; i < onlinePlayers.size(); i++) {

            if (!onlinePlayers.get(i).getName().equals(event.player.getName()))
                BWPacketHandler.INSTANCE.sendTo(new MessagePlayerMapSync(event.player.getName()), onlinePlayers.get(i));
        }

    }

    @SubscribeEvent
    public static void onPlayerLeaving(PlayerEvent.PlayerLoggedOutEvent event) {

        String username = event.player.getName();

        //sauvegarde les données du joueur
        if (BWPlayerProfileManagement.PLAYER_MAP.containsKey(username)) {
            BWPlayerProfileManagement.savePlayerData(username);

            //Sauvegarde la position du joueur lors de sa déconnection
            JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
            if (index.has(username)) {
                JsonObject profile = index.get(username).getAsJsonObject();
                JsonObject coordinates = new JsonObject();
                coordinates.addProperty("x", (int) event.player.posX);
                coordinates.addProperty("y", (int) event.player.posY);
                coordinates.addProperty("z", (int) event.player.posZ);
                profile.add("lastPos", coordinates);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = new Date();
                String dateOut = formatter.format(date);
                profile.addProperty("lastSeen", dateOut);

                index.add(username, profile);
                BWJSONUtils.writeJsonRootObject(BlackwellPlugins.PLAYERS_FILE, index);
            }
        }

        BWPacketHandler.INSTANCE.sendToAll(new MessagePlayerMapSyncDeco(event.player.getName()));
    }
}
