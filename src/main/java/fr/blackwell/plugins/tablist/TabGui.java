package fr.blackwell.plugins.tablist;

import fr.blackwell.plugins.permission.BWPlayer;
import net.minecraft.client.gui.Gui;

public class TabGui extends Gui {

    public void renderPlayerlist(int width){

        Gui.drawRect(0,500, 400, 150, 0xCC1f6eed);
    }

    public void renderSlot (BWPlayer player){

    }
}
