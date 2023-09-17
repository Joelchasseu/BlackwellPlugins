package fr.blackwell.plugins.utils;

import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class BWCommandUtils{


    public static boolean checkPermission(MinecraftServer server, ICommandSender sender, String commandName) {

        if(sender.getName().equals("@"))
            return true;

        if(sender.getName().equals(server.getName()))
            return true;

        String username = sender.getName();
        if (BWPlayerProfileManagement.PLAYER_MAP.containsKey(username)) {
            List<String> playerPerm = BWPlayerProfileManagement.PLAYER_MAP.get(username).getPermList();
            if (playerPerm.contains(commandName))
                return true;
            else return false;
        } else return false;
    }
}
