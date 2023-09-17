package fr.blackwell.plugins.command;

import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWCommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandVanish implements ICommand {
    @Override
    public String getName() {
        return "vanish";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.vanish.help";
    }

    @Override
    public List<String> getAliases() {

        List<String> aliases = new ArrayList<>();
        aliases.add("Vanish");
        aliases.add("VANISH");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();
        EntityTracker entityTracker = player.getServerWorld().getEntityTracker();

        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP && args.length == 0) {
            if (BWPlayerProfileManagement.PLAYER_MAP.containsKey(player.getName())) {
                BWPlayer playerObj = BWPlayerProfileManagement.PLAYER_MAP.get(player.getName());

                if (!playerObj.isVanished()) {
                    entityTracker.untrack(player);
                    entityTracker.updateVisibility(player);
                    playerObj.setVanished(true);
                    sender.sendMessage(new TextComponentString("Vous êtes maintenant invisible").setStyle(new Style().setColor(TextFormatting.GREEN)));
                    BWPlayerProfileManagement.PLAYER_MAP.put(player.getName(), playerObj);
                } else {
                    entityTracker.track(player);
                    entityTracker.updateVisibility(player);
                    playerObj.setVanished(false);
                    sender.sendMessage(new TextComponentString("Vous n'êtes plus invisible").setStyle(new Style().setColor(TextFormatting.GREEN)));
                    BWPlayerProfileManagement.PLAYER_MAP.put(player.getName(), playerObj);
                }
            }
        } else if (args.length == 1) {

            EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);

            if (target != null && BWPlayerProfileManagement.PLAYER_MAP.containsKey(args[0])) {

                BWPlayer targetObj = BWPlayerProfileManagement.PLAYER_MAP.get(player.getName());

                if (!targetObj.isVanished()) {
                    entityTracker.untrack(target);
                    entityTracker.updateVisibility(target);
                    targetObj.setVanished(true);
                    sender.sendMessage(new TextComponentString("Le joueur " + target.getName() + " est maintenant invisible").setStyle(new Style().setColor(TextFormatting.GREEN)));
                    BWPlayerProfileManagement.PLAYER_MAP.put(target.getName(), targetObj);
                } else {
                    entityTracker.track(target);
                    entityTracker.updateVisibility(target);
                    targetObj.setVanished(false);
                    sender.sendMessage(new TextComponentString("Le joueur " + target.getName() + " n'est plus invisible").setStyle(new Style().setColor(TextFormatting.GREEN)));
                    BWPlayerProfileManagement.PLAYER_MAP.put(target.getName(), targetObj);
                }
            } else sender.sendMessage(new TextComponentString("Erreur : Joueur introuvable").setStyle(new Style().setColor(TextFormatting.RED)));

        }else sender.sendMessage(new TextComponentString("Nombre d'arguments invalide, veuillez respecter cette structure : /vanish <username>").setStyle(new Style().setColor(TextFormatting.RED)));
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
