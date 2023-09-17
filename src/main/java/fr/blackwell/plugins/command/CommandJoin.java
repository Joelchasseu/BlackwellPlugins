package fr.blackwell.plugins.command;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.utils.BWCommandUtils;
import fr.blackwell.plugins.utils.BWJSONUtils;
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

public class CommandJoin implements ICommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.join";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("Join");
        aliases.add("JOIN");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {


        if(sender.getCommandSenderEntity() instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();
            String username = player.getName();
            JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
            JsonObject profile = index.get(username).getAsJsonObject();
            if(profile.has("lastPos")){
                JsonObject coord = profile.get("lastPos").getAsJsonObject();
                player.setPositionAndUpdate(coord.get("x").getAsInt(),coord.get("y").getAsInt(),coord.get("z").getAsInt());
                sender.sendMessage(new TextComponentString("Retour à votre dernière position, bon jeu à vous !"));
            } sender.sendMessage(new TextComponentString("Dernière position introuvable, veuillez contacter un membre du staff").setStyle(new Style().setColor(TextFormatting.RED)));
        } BlackwellPlugins.logger.warn("Seul les joueurs peuvent utiliser la commande /join");
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
