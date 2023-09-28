package fr.blackwell.plugins.utils;

import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;

import java.util.List;

public class BWCommandUtils {


    public static boolean checkPermission(MinecraftServer server, ICommandSender sender, String commandName) {

        String username = sender.getName();
        UserListOps opList = server.getPlayerList().getOppedPlayers();
        if (sender instanceof EntityPlayerMP) {

            if (opList.getEntry(((EntityPlayerMP) sender).getGameProfile()) != null)
                return true;
            else if (BWPlayerProfileManagement.PLAYER_MAP.containsKey(username)) {
                List<String> playerPerm = BWPlayerProfileManagement.PLAYER_MAP.get(username).getPermList();
                if (playerPerm.contains(commandName))
                    return true;

            } else return false;
        }
        return true;
    }
}