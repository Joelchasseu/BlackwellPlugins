package fr.blackwell.plugins.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InvSeeListener implements IInventoryChangedListener {


    private final NBTTagCompound compound;
    private final File playerData;

    public InvSeeListener(NBTTagCompound compound, File playerData){

        this.compound = compound;
        this.playerData = playerData;

    }

    @Override
    public void onInventoryChanged(IInventory targetInv) {

        NBTTagList invToSave = new NBTTagList();

        for (int i = 0; i < targetInv.getSizeInventory(); i++) {

            ItemStack stack = targetInv.getStackInSlot(i);
            if (!stack.getDisplayName().equals("Air")) {
                NBTTagCompound nbtStack = stack.writeToNBT(new NBTTagCompound());
                nbtStack.setInteger("Slot", i);

                invToSave.appendTag(nbtStack);
            }
        }

        compound.setTag("Inventory", invToSave);
        try {
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(playerData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
