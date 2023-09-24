package fr.blackwell.plugins.tablist;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TabGui extends GuiScreen {


    private final String username;
    private final BWPlayer player;
    private final EntityPlayer entityPlayer;
    private int scrollPos = 0;

    private int listOffsetY;
    private final int listPlayerHeight = 233;
    private int heightScrollBar;
    private int playerListContentY;

    private int playerNbr = 25;
    int nbrOfScroll;


    public static HashMap<String, DynamicTexture> AVATAR_MAP = new HashMap<>();
    public static HashMap<String, DynamicTexture> SKIN_MAP = new HashMap<>();

    private final HashMap<String, BWPlayer> PLAYER_MAP = (HashMap<String, BWPlayer>) BWPlayerProfileManagement.PLAYER_MAP.clone();
    private final List<String> ROLE_LIST;
    private final String[] onlinePlayers;

    private static final int FONTID = 6;

    public TabGui(EntityPlayer player) {

        this.username = player.getName();
        this.entityPlayer = player;
        this.player = PLAYER_MAP.get(username);

        allowUserInput = true;

        List<String> roleList = new ArrayList<>();
        int playerNbr = PLAYER_MAP.size();
        onlinePlayers = PLAYER_MAP.keySet().toArray(roleList.toArray(new String[0]));

        //sort players in categories
        for (int i = 0; i < PLAYER_MAP.size(); i++) {
            BWPlayer target = PLAYER_MAP.get(onlinePlayers[i]);
            String username = onlinePlayers[i];
            BWGuiUtils.bindOnlineImageAsTexture(username, "https://custom.blackwell-university.fr/skins/" + username + "/avatar.png", true);
            String role = target.getRole();

            if (!roleList.contains(role))
                roleList.add(role);
        }

        this.ROLE_LIST = roleList;

        playerListContentY = this.onlinePlayers.length * 22 + roleList.size() * 22;

        if (listPlayerHeight >= playerListContentY)
            heightScrollBar = 233;

        else {
            float factor = (float) listPlayerHeight / (float) playerListContentY;
            heightScrollBar = Math.round((float) listPlayerHeight * factor);
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        //480 x 256
        //Dimension Gui
        int xGuiWidth = 480;
        int yGuiHeight = 256;

        GL11.glPushMatrix();
        float x = (float) width / xGuiWidth;
        float y = (float) height / yGuiHeight;
        GL11.glScalef(x, y, 1.0F);
        //Background
        Gui.drawRect(0, 0, xGuiWidth, yGuiHeight, 0xffffffff);


        drawPlayerInfo((int) x, (int) y);
        drawPlayerList((int) x + 10, (int) y + 10, mouseY);
        drawWorldInfo((int) x + 190, (int) y + 120);
        drawPlayerRender((int) x + 300, (int) y + 10);

        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawCategory(String catName, int x, int y, int onlineMembers) {

        System.out.println( "Width : " + this.width + ", Height : " + this.height);

        float xSize = 155f;
        float ySize = 20f;
        GL11.glPushMatrix();
        GL11.glTranslatef(1f, 1f, 8f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FONTID);
        fontRenderer.drawStringWithShadow(catName, x + 25, (y - listOffsetY + 5), 0xFFFFFFFF);
        GL11.glPopMatrix();

        float scaleNbrOnline = 0.5f;
        GL11.glPushMatrix();
        GL11.glTranslatef(1f, 1f, 8f);
        GL11.glScalef(0.5f, 0.5f, 1f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FONTID);
        if (onlineMembers <= 1)
            fontRenderer.drawStringWithShadow(onlineMembers + " " + catName + " connecté", x + 95 * (1 / scaleNbrOnline), (y + 8 - listOffsetY) * (1 / scaleNbrOnline), 0xFFFFFFFF);
        else
            fontRenderer.drawStringWithShadow(onlineMembers + " " + catName + "s connectés", x + 95 * (1 / scaleNbrOnline), (y + 8 - listOffsetY) * (1 / scaleNbrOnline), 0xFFFFFFFF);
        GL11.glPopMatrix();

        Gui.drawRect(x, y - listOffsetY, x + (int) xSize, y - listOffsetY + (int) ySize, 0xCC424242);


    }

    public void drawSlot(BWPlayer player, String username, int x, int y) {

        int xSize = 140;
        int ySize = 20;

        String name = player.getRoleplayName();

        if (player.isStaff()) {
            float scaleStaff = 0.1f;
            GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
            GL11.glPushMatrix();
            GL11.glScalef(scaleStaff, scaleStaff, 1.0F);
            GL11.glTranslatef(1f, 1f, 5f);
            GL11.glColor4f(1, 1, 1, 1);
            this.mc.renderEngine.bindTexture(new ResourceLocation(BlackwellPlugins.MODID, "textures/gui/tablist/staffindicator.png"));
            this.drawTexturedModalRect((x + 110) * (1 / scaleStaff), (y + 3) * (1 / scaleStaff), 0, 64, 256, 128);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

        float scale = 0.8f;

        GL11.glPushMatrix();
        Gui.drawRect(x, y, x + xSize, y + ySize, 0xCC424242);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FONTID);
        GL11.glTranslatef(1.0F, 1.0F, 3.0F);
        List<String> nameTrimmed = fontRenderer.listFormattedStringToWidth(name, 115);
        if (nameTrimmed.size() == 1) {
            GL11.glScalef(scale, scale, 1.0F);
            fontRenderer.drawStringWithShadow(name, (int) ((x + 20) * (1 / scale)), (int) ((y + 6) * (1 / scale)), 0xFFFFFFFF);
        } else if (nameTrimmed.size() == 2) {
            scale = 0.8f;
            GL11.glScalef(scale, scale, 1.0F);
            for (int i = 0; i < nameTrimmed.size(); i++)
                fontRenderer.drawStringWithShadow(nameTrimmed.get(i), (int) ((x + 20) * (1 / scale)), (int) ((y + 2 + 8 * i) * (1 / scale)), 0xFFFFFFFF);
        } else {
            scale = 0.6f;
            GL11.glScalef(scale, scale, 1.0F);
            nameTrimmed = fontRenderer.listFormattedStringToWidth(name, 150);
            for (int i = 0; i < nameTrimmed.size(); i++)
                fontRenderer.drawStringWithShadow(nameTrimmed.get(i), (int) ((x + 20) * (1 / scale)), (int) ((y + 4 + 5 * i) * (1 / scale)), 0xFFFFFFFF);
        }

        GL11.glPopMatrix();


        float scaleAvatar = 0.06f;
        GL11.glPushMatrix();
        if (AVATAR_MAP.containsKey(username))
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, AVATAR_MAP.get(username).getGlTextureId());
        GL11.glScalef(scaleAvatar, scaleAvatar, 1.0F);
        GL11.glTranslatef(0F, 0F, 1F);
        this.drawTexturedModalRect((x + 2) * (1 / scaleAvatar), (y + 2) * (1 / scaleAvatar), 0, 0, 256, 256);
        GL11.glPopMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

    }

    public void drawScrollingBar(int x, int y, int mouseY, int widthList, int heightList) {

        int widthScrollBar = 5;

        //Fond de la scrolling bar
        Gui.drawRect(x + widthList - widthScrollBar, y, x + widthList, y + heightList, 0xCC757575);
        //ScrollBar
        Gui.drawRect(x + widthList - widthScrollBar, y + scrollPos, x + widthList, y + scrollPos + heightScrollBar, 0xFF000000);

    }


    public void drawPlayerList(int x, int y, int mouseY) {

        int xOffset = 0;
        int yOffset = 0;
        int width = 170;

        Gui.drawRect(x + xOffset, x + yOffset, x + xOffset + width, x + yOffset + listPlayerHeight, 0xCCff03fb);

        drawScrollingBar(x, y, mouseY, width, listPlayerHeight);
        drawList(x, y + 3, mouseY);

    }

    private void drawList(int x, int y, int mouseY) {

        int lines = 0;
        for (int i = 0; i < ROLE_LIST.size(); i++) {

            String role = ROLE_LIST.get(i);
            List<BWPlayer> memberList = new ArrayList<>();

            for (int j = 0; j < onlinePlayers.length; j++) {

                //compte les joueurs de chaque rôles
                if (PLAYER_MAP.containsKey(onlinePlayers[i])) {
                    BWPlayer target = PLAYER_MAP.get(onlinePlayers[j]);

                    if (target != null && role.equals(target.getRole()))
                        memberList.add(target);
                }
            }
            drawCategory(ROLE_LIST.get(i), x + 7, y + lines * 22 + listOffsetY, memberList.size());
            lines++;

            for (int j = 0; j < memberList.size(); j++) {

                BWPlayer target = memberList.get(j);
                if (target != null && role.equals(target.getRole())) {

                    drawSlot(target, onlinePlayers[j], x + 22, y + lines * 22 + listOffsetY);
                    lines++;
                }

            }
        }
    }

    public void drawPlayerInfo(int xAnchor, int yAnchor) {

        int width = 100;
        int height = 100;
        int yOffset = 10;
        int xOffset = 240 - width / 2;

        float scale = 0.7f;
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1F);
        GL11.glTranslatef(1f, 1f, 2f);
        BWGuiUtils.drawCenteredString("Informations Joueur :", (xOffset + (float) width / 2) * (1 / scale), yAnchor + 20.0f, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(this.player.getFirstName(), (xOffset + (float) width / 2) * (1 / scale), yAnchor + 40, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(this.player.getLastName(), (xOffset + (float) width / 2) * (1 / scale), yAnchor + 50, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(player.getAge() + " ans", (xOffset + (float) width / 2) * (1 / scale), yAnchor + 70, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(player.getRole(), (xOffset + (float) width / 2) * (1 / scale), yAnchor + 80, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(player.getMagicType(), (xOffset + (float) width / 2) * (1 / scale), yAnchor + 90, 0xFFFFFFFF);
        GL11.glPopMatrix();

        String magic = player.getMagicType().toLowerCase().replace("é","e").replace("è","e");

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1F);
        GL11.glTranslatef(0f, 1f, 2f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(BlackwellPlugins.MODID, "textures/gui/tablist/element/" + magic + ".png"));
        this.drawTexturedModalRect(xAnchor, yAnchor,0,0,400,400);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();


        Gui.drawRect(xAnchor + xOffset, yAnchor + yOffset, xAnchor + width + xOffset, yAnchor + yOffset + height, 0xCC1f6eed);
    }

    public void drawWorldInfo(int x, int y) {

        int width = 100;
        int height = 123;
        float scaleTitle = 0.8F;

        Gui.drawRect(x, y, x + width, y + height, 0xCC1f6eed);

        GL11.glPushMatrix();
        GL11.glScalef(scaleTitle, scaleTitle, 1F);
        GL11.glTranslatef(1f, 1f, 2f);
        BWGuiUtils.drawCenteredString("Derry Dalmelington", (x + (float) width / 2) * (1 / scaleTitle), y + 50.0f, 0xFFFFFFFF);
        GL11.glPopMatrix();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String time = formatter.format(new Date());
        JsonObject timeData = TimeManagement.timeData;
        String mois = timeData.get("mois").getAsString();
        String annee = timeData.get("annee").getAsString();
        String saison = timeData.get("saison").getAsString();


        float scale = 0.6f;
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1F);
        GL11.glTranslatef(1f, 1f, 2f);
        BWGuiUtils.drawCenteredString(time, (x + (float) width / 2) * (1 / scale), (y + 35.0f) * (1 / scale), 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(mois + " " + annee, (x + (float) width / 2) * (1 / scale), (y + 45.0f) * (1 / scale), 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(saison, (x + (float) width / 2) * (1 / scale), (y + 55.0f) * (1 / scale), 0xFFFFFFFF);
        GL11.glPopMatrix();
    }

    public void drawPlayerRender(int xAnchor, int yAnchor) {
        int width = 170;
        int height = 233;

        Gui.drawRect(xAnchor , yAnchor , xAnchor + width, yAnchor + height, 0xCCff03fb);
        GuiInventory.drawEntityOnScreen(0, 0, 1, 0f, 0f, this.entityPlayer);

        drawPlayerModel( xAnchor + 85, xAnchor + 30, entityPlayer);
    }

    /*@Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (BWGuiUtils.isMouseOverArea(mouseX, mouseY, 10 + 170 - 5, 10 + 1 + scrollPos, 5, 20)) {
            isScrollClicked = true;
            return;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (!Mouse.isButtonDown(0)) {
            isScrollClicked = false;
        }
    }*/

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheelState = Mouse.getEventDWheel();
        if (wheelState != 0) {
            scrollPos += wheelState > 0 ? -12 : 12;
            boundScrollBar();
            listOffsetY = Math.round((float) scrollPos / ((float) listPlayerHeight - heightScrollBar) * ((float) playerListContentY - listPlayerHeight + 22));
        }
    }

    public void drawPlayerModel(int posX, int posY, EntityLivingBase ent) {

        float scale = 150f;

        GL11.glPushMatrix();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX , (float) posY, 150.0F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(160.0F, 0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(false);
        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glPopMatrix();
    }

    private void boundScrollBar() {

        if (scrollPos < 0)
            scrollPos = 0;
        else if (scrollPos > listPlayerHeight - heightScrollBar) {
            scrollPos = listPlayerHeight - heightScrollBar;
        }
    }
}
