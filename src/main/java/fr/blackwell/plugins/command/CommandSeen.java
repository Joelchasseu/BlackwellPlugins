package fr.blackwell.plugins.command;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
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

public class CommandSeen implements ICommand {
    @Override
    public String getName() {
        return "seen";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.seen.help";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("Seen");
        aliases.add("SEEN");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if ((args.length == 1)){
            JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE).getAsJsonObject();
            if(index.has(args[0]) && index.get(args[0]).getAsJsonObject().has("lastSeen")){

                String dateIn = index.get(args[0]).getAsJsonObject().get("lastSeen").getAsString();
                String[] split = dateIn.split(" ");
                String message = "Le joueur " + args[0] + " s'est déconnecté pour la dernière fois le " + split[0] + " à " + split[1];
                sender.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.GREEN)));

            }else sender.sendMessage(new TextComponentString("Joueur " + args[0] + " ou date de dernière " +
                    "connection introuvable, veuillez vérifier le fichier players.json").setStyle(new Style().setColor(TextFormatting.RED)));
        }else sender.sendMessage(new TextComponentString("Veuillez respecter la structure de la commande :" +
                " /seen (username)").setStyle(new Style().setColor(TextFormatting.RED)));
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
