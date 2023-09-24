package fr.blackwell.plugins.tablist;

import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.common.BWGuiHandler;
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

            Minecraft.getMinecraft().player.openGui(BlackwellPlugins.instance, BWGuiHandler.TAB_PLAYER_LIST,Minecraft.getMinecraft().world,0,0,0);
        }
    }


}
