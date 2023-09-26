package fr.blackwell.plugins.chatindicator;

import fr.blackwell.plugins.tablist.BWPacketHandler;
import net.minecraft.client.gui.GuiChat;

public class ChatGui extends GuiChat {
    @Override
    public void initGui() {
        super.initGui();
        BWPacketHandler.INSTANCE.sendToServer(new MessChatOpen());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        BWPacketHandler.INSTANCE.sendToServer(new MessChatClose());
    }
}
