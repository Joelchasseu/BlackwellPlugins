package fr.blackwell.plugins.tablist;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
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


    private static final int guiWidth = 350;
    private static final int guiHeight = guiWidth * 9 / 16;
    private final BWPlayer player;
    private final EntityPlayer entityPlayer;
    private int scrollPos = 0;

    private int listOffsetY;
    private final int heightScrollBar;
    private final int playerListContentY;

    private static final ResourceLocation ELEMENT_BACKGROUND = new ResourceLocation(BlackwellPlugins.MODID, "textures/gui/tablist/element_background.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(BlackwellPlugins.MODID, "textures/gui/tablist/background.png");

    public static HashMap<String, DynamicTexture> AVATAR_MAP = new HashMap<>();
    public static HashMap<String, DynamicTexture> SKIN_MAP = new HashMap<>();
    @SuppressWarnings(value = "unchecked")
    private final HashMap<String, BWPlayer> PLAYER_MAP = (HashMap<String, BWPlayer>) BWPlayerProfileManagement.PLAYER_MAP.clone();
    private final List<String> ROLE_LIST;
    private final String[] onlinePlayers;

    private final int listPlayerHeight;

    private static final int FONT_ID = 6;

    public TabGui(EntityPlayer player) {


        String username = player.getName();
        this.entityPlayer = player;
        this.player = PLAYER_MAP.get(username);

        allowUserInput = true;

        List<String> roleList = new ArrayList<>();
        onlinePlayers = PLAYER_MAP.keySet().toArray(roleList.toArray(new String[0]));

        //sort players in categories
        for (int i = 0; i < PLAYER_MAP.size(); i++) {
            BWPlayer target = PLAYER_MAP.get(onlinePlayers[i]);
            BWGuiUtils.bindOnlineImageAsTexture(username, "https://custom.blackwell-university.fr/skins/" + username + "/avatar.png", true);
            String role = target.getRole();

            if (!roleList.contains(role))
                roleList.add(role);
        }
        this.listPlayerHeight = guiHeight - 10;

        this.ROLE_LIST = roleList;

        playerListContentY = this.onlinePlayers.length * 15 + roleList.size() * 15;

        if (listPlayerHeight >= playerListContentY)
            heightScrollBar = listPlayerHeight;


        else {
            float factor = (float) listPlayerHeight / (float) playerListContentY;
            heightScrollBar = Math.round((float) listPlayerHeight * factor);
        }
        System.out.println(listPlayerHeight + " : " + playerListContentY);

    }

    @Override
    public void initGui() {


        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();

        int centerX = this.width / 2 - guiWidth / 2;
        int centerY = this.height / 2 - guiHeight / 2;

        //Background
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(centerX, centerY, 0f, 0f, guiWidth, guiHeight, guiWidth, guiHeight);


        drawPlayerInfo(centerX + 350 / 2 - 40, centerY + 5);

        drawPlayerList(centerX + 5, centerY + 5);
        drawWorldInfo(centerX + 350 / 2 - 40, centerY + 90);
        drawPlayerRender(centerX + 220, centerY + 5);
    }

    public void drawPlayerInfo(int x, int y) {

        int width = 80;
        int height = 80;

        //Background Element
        this.mc.getTextureManager().bindTexture(ELEMENT_BACKGROUND);
        this.drawTexturedModalRect(x, y, 0, 0, width, height);

        float scale = 0.6f;
        BWGuiUtils.drawCenteredString("Informations Joueur :", x + width / 2, y + 5, scale, 0xFFFFFFFF);

        BWGuiUtils.drawCenteredString(this.player.getFirstName(), x + width / 2, y + 20, scale, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(this.player.getLastName(), x + width / 2, y + 30, scale, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(player.getAge() + " ans", x + width / 2, y + 45, scale, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(player.getRole(), x + width / 2, y + 55, scale, 0xFFFFFFFF);
        BWGuiUtils.drawCenteredString(player.getMagicType(), x + width / 2, y + 65, scale, 0xFFFFFFFF);

        //Affichage de la photo de l'élément
        /*String magic = player.getMagicType().toLowerCase().replace("é", "e").replace("è", "e");

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1F);
        GL11.glTranslatef(0f, 1f, 2f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(BlackwellPlugins.MODID, "textures/gui/tablist/element/" + magic + ".png"));
        this.drawTexturedModalRect(xAnchor, yAnchor, 0, 0, 400, 400);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();*/


    }

    public void drawCategory(String catName, int x, int y, int onlineMembers) {

        float xSize = 116f;
        float ySize = 15f;
        float scale = 1.0f;
        GL11.glPushMatrix();
        GL11.glTranslatef(1f, 1f, 8f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FONT_ID);

        List<String> nameTrimmed = fontRenderer.listFormattedStringToWidth(catName, 60);
        if (nameTrimmed.size() == 1)
            fontRenderer.drawStringWithShadow(catName, (int) ((x + 20) * (1 / scale)), (int) ((y + 3) * (1 / scale)), 0xFFFFFFFF);
        else if (nameTrimmed.size() == 2) {
            scale = 0.6f;
            GL11.glScalef(scale, scale, 1.0F);
            for (int i = 0; i < nameTrimmed.size(); i++)
                fontRenderer.drawStringWithShadow(nameTrimmed.get(i), (int) ((x + 15) * (1 / scale)), (int) ((y + 2 + 6 * i) * (1 / scale)), 0xFFFFFFFF);
        }
        GL11.glPopMatrix();
        float scaleNbrOnline = 0.5f;
        String stringRoleConnected;
        if (onlineMembers <= 1)
            stringRoleConnected = onlineMembers + " connecté";
        else stringRoleConnected = onlineMembers + "s connectés";

        BWGuiUtils.drawString(stringRoleConnected, x + 113 - Math.round(fontRenderer.getStringWidth(stringRoleConnected) * scaleNbrOnline), y + 8, scaleNbrOnline, 0xFFFFFFFF);

        Gui.drawRect(x, y, x + (int) xSize, y + (int) ySize, 0xCC424242);


    }

    public void drawSlot(BWPlayer player, String username, int x, int y) {

        int xSize = 108;
        int ySize = 15;

        String name = player.getRoleplayName();

        if (player.isStaff()) {
            float scaleStaff = 0.08f;
            GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
            GL11.glPushMatrix();
            GL11.glTranslatef(x + 86, y + 2, 5f);
            GL11.glScalef(scaleStaff, scaleStaff, 1.0F);
            GL11.glColor4f(1, 1, 1, 1);
            this.mc.renderEngine.bindTexture(new ResourceLocation(BlackwellPlugins.MODID, "textures/gui/tablist/staffindicator.png"));
            this.drawTexturedModalRect(0, 0, 0, 64, 256, 128);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

        float scale = 0.7f;

        GL11.glPushMatrix();
        Gui.drawRect(x, y, x + xSize, y + ySize, 0xCC424242);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, FONT_ID);
        GL11.glTranslatef(1.0F, 1.0F, 3.0F);
        List<String> nameTrimmed = fontRenderer.listFormattedStringToWidth(name, 100);
        if (nameTrimmed.size() == 1) {
            GL11.glScalef(scale, scale, 1.0F);
            fontRenderer.drawStringWithShadow(name, (int) ((x + 15) * (1 / scale)), (int) ((y + 4) * (1 / scale)), 0xFFFFFFFF);
        } else if (nameTrimmed.size() == 2) {
            scale = 0.6f;
            GL11.glScalef(scale, scale, 1.0F);
            for (int i = 0; i < nameTrimmed.size(); i++)
                fontRenderer.drawStringWithShadow(nameTrimmed.get(i), (int) ((x + 15) * (1 / scale)), (int) ((y + 1 + 6 * i) * (1 / scale)), 0xFFFFFFFF);
        } else {
            scale = 0.5f;
            GL11.glScalef(scale, scale, 1.0F);
            nameTrimmed = fontRenderer.listFormattedStringToWidth(name, 140);
            for (int i = 0; i < nameTrimmed.size(); i++)
                fontRenderer.drawStringWithShadow(nameTrimmed.get(i), (int) ((x + 15) * (1 / scale)), (int) ((y + 2 + 5 * i) * (1 / scale)), 0xFFFFFFFF);
        }

        GL11.glPopMatrix();


        float scaleAvatar = 0.05f;
        GL11.glPushMatrix();
        if (AVATAR_MAP.containsKey(username))
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, AVATAR_MAP.get(username).getGlTextureId());
        GL11.glTranslatef(x + 1, y + 1, 1F);
        GL11.glScalef(scaleAvatar, scaleAvatar, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        GL11.glPopMatrix();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

    }

    public void drawScrollingBar(int x, int y, int widthList, int heightList) {

        int widthScrollBar = 5;

        //Fond de la scrolling bar
        Gui.drawRect(x + widthList - widthScrollBar, y, x + widthList, y + heightList, 0xCC757575);
        //ScrollBar


        //Gui.drawRect(x + widthList - widthScrollBar, y + scrollPos, x + widthList, y + scrollPos + heightScrollBar, 0xFF000000);
        Gui.drawRect(x + widthList - widthScrollBar, y + scrollPos, x + widthList, y + scrollPos + heightScrollBar, 0xFF000000);


    }


    public void drawPlayerList(int x, int y) {

        int width = 125;
        int height = guiHeight - 10;

        this.mc.getTextureManager().bindTexture(ELEMENT_BACKGROUND);
        this.drawTexturedModalRect(x, y, 0, 0, width, height);

        drawScrollingBar(x, y, width, height);
        drawList(x, y + 3);

    }

    private void drawList(int x, int y) {

        int lines = 0;
        for (int i = 0; i < ROLE_LIST.size(); i++) {

            String role = ROLE_LIST.get(i);
            List<BWPlayer> memberList = new ArrayList<>();

            for (String onlinePlayer : onlinePlayers) {

                //compte les joueurs de chaque rôles
                if (PLAYER_MAP.containsKey(onlinePlayers[i])) {
                    BWPlayer target = PLAYER_MAP.get(onlinePlayer);

                    if (target != null && role.equals(target.getRole()))
                        memberList.add(target);
                }
            }
            if (y + 5 + lines * 17 + listOffsetY > y && y + 17 + lines * 17 + listOffsetY < y + listPlayerHeight)
                drawCategory(ROLE_LIST.get(i), x + 2, y + lines * 17 + listOffsetY, memberList.size());
            lines++;


            for (int j = 0; j < memberList.size(); j++) {

                BWPlayer target = memberList.get(j);
                if (target != null && role.equals(target.getRole())) {

                    if (y + 5 + lines * 17 + listOffsetY > y && y + 17 + lines * 17 + listOffsetY < y + listPlayerHeight)
                        drawSlot(target, onlinePlayers[j], x + 10, y + lines * 17 + listOffsetY);
                    lines++;
                }

            }
        }
    }


    public void drawWorldInfo(int x, int y) {

        int width = 80;
        int height = 101;
        float scaleTitle = 0.75F;

        this.mc.getTextureManager().bindTexture(ELEMENT_BACKGROUND);
        this.drawTexturedModalRect(x, y, 0, 0, width, height);


        BWGuiUtils.drawCenteredString("Derry Dalmelington", x + width / 2, y + 10, scaleTitle, 0xFFFFFFFF);

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

    public void drawPlayerRender(int x, int y) {
        int width = 125;
        int height = guiHeight - 10;

        this.mc.getTextureManager().bindTexture(ELEMENT_BACKGROUND);
        this.drawTexturedModalRect(x, y, 0, 0, width, height);
        drawPlayerModel(x + 60, y + 170, entityPlayer);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheelState = Mouse.getEventDWheel();
        if (wheelState != 0) {
            scrollPos += wheelState > 0 ? -5 : 5;
            boundScrollBar();
            listOffsetY = Math.round((float) scrollPos / ((float) listPlayerHeight - heightScrollBar) * ((float) playerListContentY - listPlayerHeight + 22));
        }
    }

    public void drawPlayerModel(int posX, int posY, EntityLivingBase ent) {

        float scale = 80f;
        float rotationPitch = ent.rotationPitch;
        float rotationYaw = ent.rotationYaw;
        ent.rotationPitch = 5.0f;
        ent.setRenderYawOffset(150f);
        GL11.glPushMatrix();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 150.0F);
        GlStateManager.scale(scale, scale, scale);

        GlStateManager.rotate(180f, 1F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setRenderShadow(false);
        rendermanager.setPlayerViewY(0.5f);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(false);
        ent.rotationPitch = rotationPitch;
        ent.setRenderYawOffset(rotationYaw);
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
