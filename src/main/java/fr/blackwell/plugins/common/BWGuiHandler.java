package fr.blackwell.plugins.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.tablist.TabGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class BWGuiHandler implements IGuiHandler {

    public static final int TAB_PLAYER_LIST = 0;
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == TAB_PLAYER_LIST)
            return new TabGui (player);
        return null;
    }
}
