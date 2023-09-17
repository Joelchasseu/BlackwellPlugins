package fr.blackwell.plugins.tablist;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class GuiEvent {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTabPlayerListOpened(RenderGameOverlayEvent.Pre event){
        if(event.getType() == RenderGameOverlayEvent.ElementType.PLAYER_LIST){
            event.setCanceled(true);

            TabGui tabList = new TabGui();
            tabList.renderPlayerlist(Minecraft.getMinecraft().displayWidth);
        }
    }
}
