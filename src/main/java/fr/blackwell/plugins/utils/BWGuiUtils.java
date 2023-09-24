package fr.blackwell.plugins.utils;

import fr.blackwell.plugins.tablist.TabGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class BWGuiUtils {

    private static final int FONT_ID = 6;

    public static final FontRenderer font = Minecraft.getMinecraft().fontRenderer;

    public static void drawCenteredString(String text, int x, int y, int color) {
        font.drawStringWithShadow(text, x - ((float) font.getStringWidth(text) / 2), y, color);
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FONT_ID);
        font.drawString(text, x - ((float) font.getStringWidth(text) / 2), y, color, true);
    }

    public static void bindOnlineImageAsTexture(String username, String urlString, boolean avatar) {

        try {
            URL url = new URL(urlString);

            DynamicTexture image = new DynamicTexture(ImageIO.read(url));
            if (avatar)
                TabGui.AVATAR_MAP.put(username, image);
            else
                TabGui.SKIN_MAP.put(username, image);

        } catch (IOException e) {
        }
    }

    public static boolean isMouseOverArea(int mouseX, int mouseY, int posX, int posY, int sizeX, int sizeY) {
        return (mouseX >= posX && mouseX < posX + sizeX && mouseY >= posY && mouseY < posY + sizeY);
    }

    public static void drawRectWithOnlinePic(int x, int y, int width, int height, String urlString) throws IOException {

    }
}
