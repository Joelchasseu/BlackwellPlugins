package fr.blackwell.plugins.chatindicator;

import fr.blackwell.plugins.BlackwellPlugins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class ChatEvent {

    public static ArrayList<String> CHATTING_PLAYER_LIST = new ArrayList<>();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onPlayersChatOpened(GuiOpenEvent event) {
        if (event.getGui() != null && event.getGui() instanceof GuiChat)
            event.setGui(new ChatGui());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void playerRender(RenderPlayerEvent.Pre event) {

        double x = event.getEntityPlayer().posX - Minecraft.getMinecraft().player.posX;
        double y = event.getEntityPlayer().posY - Minecraft.getMinecraft().player.posY;
        double z = event.getEntityPlayer().posZ - Minecraft.getMinecraft().player.posZ;

        if (CHATTING_PLAYER_LIST.contains(event.getEntityPlayer().getDisplayNameString())
                && event.getEntityPlayer().getDisplayNameString().equals(Minecraft.getMinecraft().player.getDisplayNameString())) {
            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(BlackwellPlugins.MODID, "textures/chatIndicator.png"));
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0, event.getEntityPlayer().height + 0.5, 0);
            GlStateManager.color(1, 1, 1);

            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(-0.016, -0.016, 0.016);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder renderer = tessellator.getBuffer();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            renderer.pos(-20, 20, 0.0F).tex(1, 1).endVertex();// coin inférieur gauche
            renderer.pos(20, 20, 0).tex(1, 0).endVertex();// coin inférieur droit
            renderer.pos(20, -20, 0).tex(0, 0).endVertex();// coin supérieur droit
            renderer.pos(-20, -20, 0).tex(0, 1).endVertex();// coin supérieur gauche

            tessellator.draw();
            GlStateManager.enableAlpha();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();

        } else if (CHATTING_PLAYER_LIST.contains(event.getEntityPlayer().getDisplayNameString())
                && !event.getEntityPlayer().getDisplayNameString().equals(Minecraft.getMinecraft().player.getDisplayNameString())) {

            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(BlackwellPlugins.MODID, "textures/chatIndicator.png"));
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0, event.getEntityPlayer().height + 0.5, 0);
            GlStateManager.color(1, 1, 1);

            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(-0.016, -0.016, 0.016);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder renderer = tessellator.getBuffer();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            renderer.pos(-20, 20, 0.0F).tex(1, 1).endVertex();// coin inférieur gauche
            renderer.pos(20, 20, 0).tex(1, 0).endVertex();// coin inférieur droit
            renderer.pos(20, -20, 0).tex(0, 0).endVertex();// coin supérieur droit
            renderer.pos(-20, -20, 0).tex(0, 1).endVertex();// coin supérieur gauche

            tessellator.draw();
            GlStateManager.enableAlpha();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();

        }
    }
}
