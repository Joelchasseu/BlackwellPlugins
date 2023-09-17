package fr.blackwell.plugins.command;

import fr.blackwell.plugins.utils.BWCommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandNear implements ICommand {
    @Override
    public String getName() {
        return "near";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "bw.command.near";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("Near");
        aliases.add("NEAR");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        List<String> nearPlayer = new ArrayList<>();
        if (args.length == 0)
            sender.sendMessage(new TextComponentString("Veuillez choisir un nombre comme argument /near <number>").setStyle(new Style().setColor(TextFormatting.RED)));
        else if (args.length == 1) {
            List<EntityPlayerMP> onlinePlayers = server.getPlayerList().getPlayers();

            for (int i = 0; i < onlinePlayers.size(); i++) {
                if (sender.getPosition().distanceSq(onlinePlayers.get(i).getPosition()) <= Integer.parseInt(args[0]) * Integer.parseInt(args[0]) && !onlinePlayers.get(i).getName().equals(sender.getName())) {
                    nearPlayer.add(onlinePlayers.get(i).getName());
                }
            }

            if (nearPlayer.isEmpty()) {
                String message = "Aucun joueurs n'est présent dans un rayon de " + args[0] + " cubes.";
                sender.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.GREEN)));
            } else {

                String message;
                String messageList;

                if (nearPlayer.size() == 1) {

                    message = nearPlayer.size() + " joueur est présent dans un rayon de " + args[0] + " cubes.";
                    messageList = "Joueur présent : " + nearPlayer.get(0);

                } else {

                    message = nearPlayer.size() + " joueurs sont présent dans un rayon de " + args[0] + " cubes.";

                    messageList = "Joueurs présents : " + nearPlayer.get(0);
                    for (int i = 1; i < nearPlayer.size(); i++)
                        messageList += ", " + nearPlayer.get(i);
                }

                sender.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.GREEN)));
                sender.sendMessage(new TextComponentString(messageList).setStyle(new Style().setColor(TextFormatting.GREEN)));
            }
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
