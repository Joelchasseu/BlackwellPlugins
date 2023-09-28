package fr.blackwell.plugins;

import fr.blackwell.plugins.chat.ChatProcessing;
import fr.blackwell.plugins.chatindicator.ChatEvent;
import fr.blackwell.plugins.command.*;
import fr.blackwell.plugins.common.BWGuiHandler;
import fr.blackwell.plugins.events.EventsPerm;
import fr.blackwell.plugins.permission.BWPermissionHandler;
import fr.blackwell.plugins.permission.BWPermissionManagement;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.tablist.BWPacketHandler;
import fr.blackwell.plugins.tablist.GuiEvent;
import fr.blackwell.plugins.tablist.TimeManagement;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = BlackwellPlugins.MODID, name = BlackwellPlugins.NAME, version = BlackwellPlugins.VERSION)
public class BlackwellPlugins
{
    public static final String MODID = "blackwellplugins";
    public static final String NAME = "Blackwell University Plugins";
    public static final String VERSION = "0.1";
    public static final File PERM_FILE = new File("config/blackwell-plugins/permissions.json");
    public static final File PLAYERS_FILE = new File("config/blackwell-plugins/players.json");
    public static final File CHAT_CONFIG_FILE = new File("config/blackwell-plugins/chatconfig.json");
    public static final File WARP_FILE = new File("config/blackwell-plugins/warp.json");
    public static final File TIME_FILE = new File("config/blackwell-plugins/time.json");

    public static BWPermissionHandler permManager = new BWPermissionHandler();

    public static Logger logger;

    @Mod.Instance(MODID)
    public static BlackwellPlugins instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        PermissionAPI.setPermissionHandler(permManager);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(EventsPerm.class);
        MinecraftForge.EVENT_BUS.register(ChatProcessing.class);
        MinecraftForge.EVENT_BUS.register(GuiEvent.class);
        MinecraftForge.EVENT_BUS.register(ChatEvent.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(BlackwellPlugins.instance, new BWGuiHandler());

        BWPacketHandler.registerMessages();
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandBWPermission());
        event.registerServerCommand(new CommandWarp());
        event.registerServerCommand(new CommandJoin());
        event.registerServerCommand(new CommandSeen());
        event.registerServerCommand(new CommandInvSee());
        event.registerServerCommand(new CommandRoll());
        event.registerServerCommand(new CommandNear());
        event.registerServerCommand(new CommandVanish());
        event.registerServerCommand(new CommandEditProfile());
        BWPermissionManagement.loadPerm();

        ChatProcessing.loadChatCanals();

        //toujours garder à la fin du bloc
        permManager.sortOPReservedCommands(event.getServer());
    }
    @EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event){

        BWJSONUtils.createNewJsonServerSide(PERM_FILE);
        BWPlayerProfileManagement.createPlayersJSON(PLAYERS_FILE);
        BWJSONUtils.createNewJsonServerSide(WARP_FILE);
        TimeManagement.InitiateTime();
        ChatProcessing.createChatConfigFile();
    }
}
