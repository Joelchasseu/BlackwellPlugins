package fr.blackwell.plugins.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.utils.BWCommandUtils;
import fr.blackwell.plugins.utils.BWJSONUtils;
import fr.blackwell.plugins.utils.BWUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandWarp implements ICommand {
    @Override
    public String getName() {
        return "bwwarp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.warp.help";
    }

    @Override
    public List<String> getAliases() {

        List<String> aliases = new ArrayList<String>();
        aliases.add("warp");

        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        Entity entity = sender.getCommandSenderEntity();

        JsonObject index;

        if (!BWJSONUtils.getJsonRootObject(BlackwellPlugins.WARP_FILE).isJsonNull())
            index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.WARP_FILE);
        else index = new JsonObject();


        if((args.length == 5 && args[0].equals("set")) && !((args[1]).equals("lobby") || args[1].equals("list"))){

            if(BWUtils.isNumeric(args[2]) && BWUtils.isNumeric(args[3]) && BWUtils.isNumeric(args[4])){

                int x = Integer.parseInt(args[2]);
                int y = Integer.parseInt(args[3]);
                int z = Integer.parseInt(args[4]);

                JsonObject coordinates = new JsonObject();
                coordinates.addProperty("name", args[1]);
                coordinates.addProperty("x", x);
                coordinates.addProperty("y", y);
                coordinates.addProperty("z", z);
                index.add(args[1], coordinates);

                sender.sendMessage(new TextComponentString("Warp " + args[1] + " enregistré aux coordonnées : x=" + x + " y=" + y + " z=" + z).setStyle(new Style().setColor(TextFormatting.GREEN)));

                JsonArray list = getWarpListWithNewWarp(index, args[1]);
                index.add("list", list);

                BWJSONUtils.writeJsonRootObject(BlackwellPlugins.WARP_FILE, index);
            }else sender.sendMessage(new TextComponentString("Veuillez utiliser la structure suivante : \"/warp set x y z\" et vérifier que x y z soit bien des nombres ").setStyle(new Style().setColor(TextFormatting.RED)));
        }

        if ((args.length == 2 && args[0].equals("remove")) && !args[1].equals("list")) {

            JsonArray list = getWarpListWithoutAWarp(index, args[1]);
            index.add("list", list);
            index.remove(args[1]);

            BWJSONUtils.writeJsonRootObject(BlackwellPlugins.WARP_FILE, index);

            sender.sendMessage(new TextComponentString("Warp " + args[1] + " a bien été supprimé de la liste des warps").setStyle(new Style().setColor(TextFormatting.GREEN)));

        }


        if (entity instanceof EntityPlayerMP) {

            if (args.length == 1 && !args[0].equals("setlobby") && !args[0].equals("list")) {
                if (index.has(args[0])) {

                    JsonObject warp = index.get(args[0]).getAsJsonObject();

                    int x = warp.get("x").getAsInt();
                    int y = warp.get("y").getAsInt();
                    int z = warp.get("z").getAsInt();

                    EntityPlayerMP entityMP = (EntityPlayerMP) entity;
                    entityMP.setPositionAndUpdate(x, y, z);
                    sender.sendMessage(new TextComponentString("Vous avez bien été téléporté au point : " + args[0]).setStyle(new Style().setColor(TextFormatting.GREEN)));

                } else
                    sender.sendMessage(new TextComponentString("Le warp " + args[0] + " est introuvable, essayez /warp list pour voir la liste des warps disponibles").setStyle(new Style().setColor(TextFormatting.RED)));

            }
            if (args.length == 1 && args[0].equals("setlobby")) {

                int x = (int) Math.round(entity.posX);
                int y = (int) Math.round(entity.posY);
                int z = (int) Math.round(entity.posZ);

                JsonObject coordinates = new JsonObject();
                coordinates.addProperty("x", x);
                coordinates.addProperty("y", y);
                coordinates.addProperty("z", z);
                index.add("lobby", coordinates);

                JsonArray list = getWarpListWithNewWarp(index, "lobby");
                index.add("list", list);

                BWJSONUtils.writeJsonRootObject(BlackwellPlugins.WARP_FILE, index);
                sender.sendMessage(new TextComponentString("Lobby défini aux coordonnées : x=" + x + " y=" + y + " z=" + z).setStyle(new Style().setColor(TextFormatting.GREEN)));

            } else if ((args.length == 2 && args[0].equals("set")) && !((args[1]).equals("lobby") || args[1].equals("list"))) {
                //Set d'un warp
                int x = (int) Math.round(entity.posX);
                int y = (int) Math.round(entity.posY);
                int z = (int) Math.round(entity.posZ);

                JsonObject coordinates = new JsonObject();
                coordinates.addProperty("name", args[1]);
                coordinates.addProperty("x", x);
                coordinates.addProperty("y", y);
                coordinates.addProperty("z", z);
                index.add(args[1], coordinates);

                sender.sendMessage(new TextComponentString("Warp " + args[1] + " enregistré aux coordonnées : x=" + x + " y=" + y + " z=" + z).setStyle(new Style().setColor(TextFormatting.GREEN)));

                JsonArray list = getWarpListWithNewWarp(index, args[1]);
                index.add("list", list);

                BWJSONUtils.writeJsonRootObject(BlackwellPlugins.WARP_FILE, index);
            } else if (args.length == 1 && args[0].equals("list")) {
                if (index.has("list") && index.get("list").getAsJsonArray().size() != 0) {
                    JsonArray list = index.get("list").getAsJsonArray();
                    String message = "Liste des warp disponibles : " + list.get(0);
                    if (list.size() > 1) {
                        for (int i = 1; i < list.size(); i++) {
                            message += ", " + list.get(i).getAsString();
                        }
                    }
                    sender.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(TextFormatting.GREEN)));
                }
            }
        }
    }

    public static JsonArray getWarpListWithNewWarp(JsonObject index, String warpName) {

        JsonArray list;

        if (index.has("list")) {
            list = index.get("list").getAsJsonArray();
        } else {
            list = new JsonArray();
        }

        boolean alreadyInList = false;

        if (list.size() != 0)
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAsString().equals(warpName))
                    alreadyInList = true;
            }

        if (!alreadyInList)
            list.add(warpName);

        return list;
    }

    public static JsonArray getWarpListWithoutAWarp(JsonObject index, String warpName) {

        JsonArray list;

        if (index.has("list")) {
            list = index.get("list").getAsJsonArray();
        } else {
            list = new JsonArray();
            return list;
        }



        if (list.size() != 0)
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAsString().equals(warpName))
                    list.remove(i);
            }

        return list;
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
