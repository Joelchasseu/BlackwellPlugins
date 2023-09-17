package fr.blackwell.plugins.command;

import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandRoll implements ICommand {
    @Override
    public String getName() {
        return "roll";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "bw.command.roll";
    }

    @Override
    public List<String> getAliases() {

        List<String> aliases = new ArrayList<>();
        aliases.add("Roll");
        aliases.add("ROLL");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        Random rand = new Random();
        String message = "";
        int defaultRadius = 15;

        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {

            EntityPlayerMP senderEntity = (EntityPlayerMP) sender.getCommandSenderEntity();
            String displayName = BWPlayerProfileManagement.PLAYER_MAP.get(senderEntity.getName()).getRoleplayName();

            int diceResult = 0;

            if (args.length == 0) {
                diceResult = rand.nextInt(100) + 1;
                message = displayName + " a lancé 1 dé 100 et à obtenu ";

                TextComponentString messToSend = new TextComponentString(TextFormatting.GRAY + message + getRollColor(diceResult, 100) + diceResult);
                sendResults(messToSend, defaultRadius, senderEntity);


            } else if (args.length == 1 && (args[0].matches("\\d+d\\d+[+-]\\d+") || args[0].matches("\\d+d\\d+"))) {

                int nbrDes = 0;
                int nbrFaces = 0;
                int bonusMalus = 0;
                if (args[0].matches("\\d+d\\d+")) {
                    String[] split = args[0].split("d");
                    nbrDes = Integer.parseInt(split[0]);
                    nbrFaces = Integer.parseInt(split[1]);
                } else if (args[0].matches("\\d+d\\d+[+-]\\d+")) {
                    String[] split = args[0].split("d");
                    nbrDes = Integer.parseInt(split[0]);
                    if (split[1].contains("+")) {
                        split = split[1].split("\\+");
                        nbrFaces = Integer.parseInt(split[0]);
                        bonusMalus = Integer.parseInt(split[1]);
                    } else {
                        split = split[1].split("-");
                        nbrFaces = Integer.parseInt(split[0]);
                        bonusMalus = -Integer.parseInt(split[1]);
                    }
                }

                if (nbrDes > 1 && nbrDes <= 5)
                    sendResults(new TextComponentString(TextFormatting.GRAY + "-----Détail-----"), defaultRadius, senderEntity);

                for (int i = 0; i < nbrDes; i++) {
                    int dice = rand.nextInt(nbrFaces) + 1;

                    if (nbrDes > 1 && nbrDes <= 5) {
                        TextComponentString tempResult = new TextComponentString(TextFormatting.GRAY + "1d" + nbrFaces + " a obtenu " + getRollColor(dice, nbrFaces) + dice);
                        sendResults(tempResult, defaultRadius, senderEntity);
                    }
                    diceResult += dice;
                }

                diceResult += bonusMalus;
                if (nbrDes > 1 && nbrDes <= 5) {
                    String bonusMalusString = "";
                    if (bonusMalus != 0) {
                        if (bonusMalus < 0) bonusMalusString = "Malus de ";
                        if (bonusMalus > 0) bonusMalusString = "Bonus de +";
                        sendResults(new TextComponentString(TextFormatting.GRAY + bonusMalusString + bonusMalus), defaultRadius, senderEntity);
                    }

                    sendResults(new TextComponentString(TextFormatting.GRAY + "----Résultat----"), defaultRadius, senderEntity);
                }

                if (bonusMalus == 0)
                    message = displayName + " a lancé " + nbrDes + "d" + nbrFaces + " et a obtenu ";
                else if (bonusMalus > 0)
                    message = displayName + " a lancé " + nbrDes + "d" + nbrFaces + "+" + bonusMalus + " et a obtenu ";
                else
                    message = displayName + " a lancé " + nbrDes + "d" + nbrFaces + bonusMalus + " et a obtenu ";

                TextComponentString messToSend = new TextComponentString(TextFormatting.GRAY + message + getRollColor(diceResult, nbrDes * nbrFaces) + diceResult);
                sendResults(messToSend, defaultRadius, senderEntity);

            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Erreur veuillez suivre le format suivant /roll < (nbr de dés)d(nbr de faces), ex : /roll 2d6 lancera 2 dés à 6 faces"));
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return false;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    public static void sendResults(TextComponentString message, int radius, EntityPlayerMP sender) {

        List<EntityPlayerMP> playerInRange = new ArrayList<>();
        List<EntityPlayerMP> onlinePlayers = FMLServerHandler.instance().getServer().getPlayerList().getPlayers();

        for (int i = 0; i < onlinePlayers.size(); i++)
            if (sender.getPosition().distanceSq(onlinePlayers.get(i).getPosition()) <= (radius * radius))
                playerInRange.add(onlinePlayers.get(i));

        for (int i = 0; i < playerInRange.size(); i++)
            playerInRange.get(i).sendMessage(message);
    }

    public static TextFormatting getRollColor(int diceResult, int maxRange) {

        TextFormatting resultColor;
        if (diceResult <= Math.round(maxRange * 0.20))
            resultColor = TextFormatting.GREEN;
        else if (diceResult < Math.round(maxRange * 0.80))
            resultColor = TextFormatting.GOLD;
        else
            resultColor = TextFormatting.RED;

        return resultColor;
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
