package fr.blackwell.plugins.utils;

import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.tablist.TabGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class BWGuiUtils {

    public static final ResourceLocation FONT_ID = new ResourceLocation("textures/font/ascii.png");

    public static final FontRenderer font = Minecraft.getMinecraft().fontRenderer;

    public static void drawString(String text, int x, int y, float scale,int color) {
        GL11.glPushMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().renderEngine.getTexture(FONT_ID).getGlTextureId());
        GL11.glTranslatef(x, y, 2f);
        GL11.glScalef(scale, scale, 1F);
        font.drawStringWithShadow(text, 0, 0, color);
        GL11.glPopMatrix();
    }
    public static void drawCenteredString(String text, int x, int y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().renderEngine.getTexture(FONT_ID).getGlTextureId());
        int stringWidth = Math.round(font.getStringWidth(text) * scale);
        GL11.glTranslatef(x - stringWidth / 2f, y, 2f);
        GL11.glScalef(scale, scale, 1F);
        font.drawStringWithShadow(text, 0, 0, color);
        GL11.glPopMatrix();
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().renderEngine.getTexture(FONT_ID).getGlTextureId());
        font.drawString(text, x - ((float) font.getStringWidth(text) / 2), y, color, true);
    }

    public static void bindOnlineImageAsTexture(String username, String urlString) {

        try {
            URL url = new URL(urlString);

            DynamicTexture image = new DynamicTexture(ImageIO.read(url));
                TabGui.AVATAR_MAP.put(username, image);
            BlackwellPlugins.logger.info("L'image pour le joueur : " + username + " a bien été chargée");


        } catch (IOException e) {
            BlackwellPlugins.logger.warn("Impossible de charger l'image pour le joueur : " + username);
        }
    }

    public static void bindDefaultFont(){
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().renderEngine.getTexture(FONT_ID).getGlTextureId());
    }

    public static boolean isMouseOverArea(int mouseX, int mouseY, int posX, int posY, int sizeX, int sizeY) {
        return (mouseX >= posX && mouseX < posX + sizeX && mouseY >= posY && mouseY < posY + sizeY);
    }
}
