package fr.blackwell.plugins.command;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.inventory.InvSeeListener;
import fr.blackwell.plugins.utils.BWCommandUtils;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CommandInvSee implements ICommand {
    @Override
    public String getName() {
        return "invsee";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.invsee.help";
    }

    @Override
    public List<String> getAliases() {

        List<String> aliases = new ArrayList<>();
        aliases.add("inv");
        aliases.add("is");
        aliases.add("InvSee");
        aliases.add("invSee");
        aliases.add("Invsee");
        aliases.add("INVSEE");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP senderEntity = (EntityPlayerMP) sender.getCommandSenderEntity();

            if (args.length == 1) {
                JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);

                if (index.has(args[0])) {

                    JsonObject profile = index.get(args[0]).getAsJsonObject();
                    String uuid = profile.get("uuid").getAsString();
                    SaveHandler saveHandler = (SaveHandler) server.getWorld(senderEntity.dimension).getSaveHandler();
                    String path = saveHandler.getWorldDirectory().getPath() + "/playerdata/" + uuid + ".dat";
                    File playerData = new File(path);

                    if (server.getPlayerList().getPlayerByUsername(args[0]) != null && sender.getName().equals(args[0])) {
                        //ouvrir l'inventaire d'une personne connectée
                        EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);

                        IInventory targetInv = target.inventory;
                        senderEntity.displayGUIChest(targetInv);


                    }//ouvrir l'inventaire d'une personne déconnectée
                    else if (playerData.exists()) {

                        NBTTagCompound compound;

                        try {
                            compound = CompressedStreamTools.readCompressed(new FileInputStream(playerData));
                            NBTTagList inventory = compound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);



                            InventoryBasic targetInv = new InventoryBasic("Inventaire de " + args[0], true, 42);

                            NBTTagList invToSave = new NBTTagList();

                            for (int i = 0; i < inventory.tagCount(); i++) {

                                ItemStack stack = new ItemStack(inventory.getCompoundTagAt(i));

                                int slot = inventory.getCompoundTagAt(i).getInteger("Slot");
                                if (slot <= 35)
                                    targetInv.setInventorySlotContents(slot, stack);
                                else {
                                    invToSave.appendTag(inventory.getCompoundTagAt(i));
                                }
                            }

                            InvSeeListener listener = new InvSeeListener(compound, playerData);

                            targetInv.addInventoryChangeListener(listener);

                            senderEntity.displayGUIChest(targetInv);

                            for (int i = 0; i < targetInv.getSizeInventory(); i++) {

                                ItemStack stack = targetInv.getStackInSlot(i);
                                if (!stack.getDisplayName().equals("Air")) {
                                 NBTTagCompound nbtStack = stack.writeToNBT(new NBTTagCompound());
                                 nbtStack.setInteger("Slot", i);

                                 invToSave.appendTag(nbtStack);
                                }
                            }
                            compound.setTag("Inventory", invToSave);
                            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(playerData));
                            //Player917

                        } catch (Exception e) {
                            e.printStackTrace();
                            BlackwellPlugins.logger.warn("Impossible de lire le fichier " + playerData.toPath());
                        }
                    }
                } else
                    sender.sendMessage(new TextComponentString("Joueur non trouvé").setStyle(new Style().setColor(TextFormatting.RED)));
            }
        } else
            BlackwellPlugins.logger.warn("La commande /invsee ne peut être executée depuis la console du serveur ou une entité autre qu'un joueur");
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
