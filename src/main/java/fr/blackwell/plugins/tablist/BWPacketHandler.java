package fr.blackwell.plugins.tablist;

import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.chatindicator.MessChatClose;
import fr.blackwell.plugins.chatindicator.MessChatCloseAns;
import fr.blackwell.plugins.chatindicator.MessChatOpen;
import fr.blackwell.plugins.chatindicator.MessChatOpenAns;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BWPacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(BlackwellPlugins.MODID);

    public static void registerMessages(){

        INSTANCE.registerMessage(MessagePlayerMapSync.Handler.class, MessagePlayerMapSync.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MessagePlayerMapSyncConnection.Handler.class, MessagePlayerMapSyncConnection.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MessagePlayerMapSyncDeco.Handler.class, MessagePlayerMapSyncDeco.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(MessageSyncTime.Handler.class, MessageSyncTime.class, 3, Side.CLIENT );
        INSTANCE.registerMessage(MessChatOpen.Handler.class, MessChatOpen.class, 4, Side.SERVER );
        INSTANCE.registerMessage(MessChatOpenAns.Handler.class, MessChatOpenAns.class, 5, Side.CLIENT );
        INSTANCE.registerMessage(MessChatClose.Handler.class, MessChatClose.class, 6, Side.SERVER );
        INSTANCE.registerMessage(MessChatCloseAns.Handler.class, MessChatCloseAns.class, 7, Side.CLIENT );

    }
}
