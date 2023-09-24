package fr.blackwell.plugins.command;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
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

public class CommandEditProfile implements ICommand {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.edit.help";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("Edit");
        aliases.add("EDIT");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if(args.length == 3){
            String usernameTarget = args[0];
            String tagToChange = args[1];
            String newValue = args[2];

            JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
            if(index.has(usernameTarget)){
                JsonObject profile = index.get(usernameTarget).getAsJsonObject();
                if(profile.has(tagToChange)){
                    profile.addProperty(tagToChange, newValue);
                    index.add(usernameTarget, profile);
                    BWJSONUtils.writeJsonRootObject(BlackwellPlugins.PLAYERS_FILE, index);
                    BWPlayerProfileManagement.loadPlayerData(usernameTarget);

                    sender.sendMessage(new TextComponentString("La propriété du joueur " + usernameTarget + " nommée " + tagToChange + " est désormais définie sur : " + newValue).setStyle(new Style().setColor(TextFormatting.GREEN)));
                } else sender.sendMessage(new TextComponentString("Le tag " + tagToChange + " est introuvable pour le joueur " + usernameTarget).setStyle(new Style().setColor(TextFormatting.RED)));
            } else sender.sendMessage(new TextComponentString("Le joueur " + usernameTarget + " est introuvable dans le fichier players.json").setStyle(new Style().setColor(TextFormatting.RED)));
        } else sender.sendMessage(new TextComponentString("Execution impossible, veuillez respecter la structure suivante : /edit <username> <tagToChange> <newValue>").setStyle(new Style().setColor(TextFormatting.RED)));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return BWCommandUtils.checkPermission(server,sender,this.getName());
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
