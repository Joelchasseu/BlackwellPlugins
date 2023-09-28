package fr.blackwell.plugins.command;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPermissionManagement;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.permission.Group;
import fr.blackwell.plugins.tablist.BWPacketHandler;
import fr.blackwell.plugins.tablist.MessagePlayerMapSyncConnection;
import fr.blackwell.plugins.utils.BWCommandUtils;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CommandBWPermission implements ICommand {

    private static List<String> aliases;

    public CommandBWPermission() {

        aliases = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "bwp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.bwp.help";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {


        if (args[0].equals("reload") && args.length == 1) {
            BWPermissionManagement.savePerm();
            BWPermissionManagement.loadPerm();
        }

        if ( args.length == 2 && args[0].equals("group") && args[1].equals("list")) {
            String[] keySet = BWPermissionManagement.getGroupMap().keySet().toArray(new String[0]);
            Map<String, Group> groupMap = BWPermissionManagement.getGroupMap();

            if (!groupMap.isEmpty()) {
                String groupString = "Groupes disponibles : " + groupMap.get(keySet[0]).getName();
                for (int i = 1; i < keySet.length; i++)
                    groupString += ", " + groupMap.get(keySet[i]).getName();

                sender.sendMessage(new TextComponentString(groupString).setStyle(new Style().setColor(TextFormatting.GREEN)));
            } else
                sender.sendMessage(new TextComponentString("Aucuns groupes configurés, veuillez completer le fichier permissions.json").setStyle(new Style().setColor(TextFormatting.GREEN)));
        }

        if (args.length == 4 && args[1].equals("group")) {
            String username = args[0];
            JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
            if (index.has(username)) {
                String groupName = args[3];
                if (BWPermissionManagement.isGroupName(groupName)) {
                    if (args[2].equals("add")) {

                        BWPlayerProfileManagement.addGroupToPlayer(username, groupName);
                        sender.sendMessage(new TextComponentString("Le joueur " + username + " a été ajouté au groupe " + groupName).setStyle(new Style().setColor(TextFormatting.GREEN)));
                        BWPacketHandler.INSTANCE.sendToAll(new MessagePlayerMapSyncConnection());

                    } else if (args[2].equals("remove")) {

                        BWPlayerProfileManagement.removeGroupToPlayer(username, groupName);
                        sender.sendMessage(new TextComponentString("Le joueur " + username + " a été retiré du groupe " + groupName).setStyle(new Style().setColor(TextFormatting.GREEN)));
                        BWPacketHandler.INSTANCE.sendToAll(new MessagePlayerMapSyncConnection());

                    } else sender.sendMessage(new TextComponentString("Commande invalide, essayez /bwp <username> " +
                            "group (add|remove) <groupName>").setStyle(new Style().setColor(TextFormatting.RED)));

                } else sender.sendMessage(new TextComponentString("Le groupe spécifié n'existe pas, essayez /bwp " +
                        "group list pour obtenir la liste des groupes disponibles.").setStyle(new Style().setColor(TextFormatting.RED)));

            } else sender.sendMessage(new TextComponentString("Le joueur spécifié n'existe pas, le joueur doit être " +
                    "présent dans le fichier players.json").setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return BWCommandUtils.checkPermission(server, sender, this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
