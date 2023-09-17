package fr.blackwell.plugins.events;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mod.EventBusSubscriber
public class EventsPerm {


    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public static void onPlayerConnection(PlayerEvent.PlayerLoggedInEvent event) {
        //event.player.getName()
        String username = event.player.getName();
        boolean hasProfile = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE).has(username);

        if (event.player.getName().startsWith("Player") && hasProfile) {

            BlackwellPlugins.logger.info("Le profil du joueur " + username + "a bien été crée");
            BWPlayerProfileManagement.loadPlayerData(username);

        } else {
            JsonObject profile = BWJSONUtils.getPlayerBlackWellDataFromWebsite(username);
            profile.addProperty("uuid", event.player.getGameProfile().getId().toString());
            if (profile.has("firstname")) {
                BWPlayerProfileManagement.writePlayerBWProfile(profile, username);
                BlackwellPlugins.logger.info("Le profil du joueur " + username + "a bien été crée");
                BWPlayerProfileManagement.loadPlayerData(username);
            } else {
                /*if (event.player.getServer().isSinglePlayer())
                    FMLServerHandler.instance().getServer().commandManager.executeCommand(event.player.getServer(), "/kick " + event.player.getName() + " Veuillez vous créer un profil via le customiseur sur le site de Blackwell University (https://blackwell-university.fr/index.html#who_are_we)");
                FMLServerHandler.instance().getServer().commandManager.executeCommand(event.player.getServer(), "/kick " + event.player.getName() + " Veuillez vous créer un profil via le customiseur sur le site de Blackwell University (https://blackwell-university.fr/index.html#who_are_we)");
                 */

                profile.addProperty("firstname", "Sanglier");
                profile.addProperty("lastname", "De Cornouailles");
                BWPlayerProfileManagement.writePlayerBWProfile(profile, username);
                BlackwellPlugins.logger.info("Le profil du joueur " + username + "a bien été crée");
                BWPlayerProfileManagement.loadPlayerData(username);
            }
        }

        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.WARP_FILE);
        if (index.has("lobby")) {
            JsonObject lobbyCoords = index.get("lobby").getAsJsonObject();
            if (lobbyCoords.has("x") && lobbyCoords.has("y") && lobbyCoords.has("z")) {

                int x = lobbyCoords.get("x").getAsInt();
                int y = lobbyCoords.get("y").getAsInt();
                int z = lobbyCoords.get("z").getAsInt();
                event.player.getServer().getPlayerList().getPlayerByUsername(username).setPositionAndUpdate(x, y, z);
            }
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
                BWJSONUtils.writeJsonRootObject(BlackwellPlugins.PLAYERS_FILE,index);
            }
        }
    }

    @SubscribeEvent
    public static void ClosingTargetInv (PlayerContainerEvent.Close event){


    }
}
