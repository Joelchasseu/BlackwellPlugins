package fr.blackwell.plugins.permission;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.command.CommandWrapper;
import fr.blackwell.plugins.utils.BWJSONUtils;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class BWPermissionHandler implements IPermissionHandler {

    public static final GameProfile defaultPlayerProfile = new GameProfile(new UUID(1234567987, 123545787), "_permtest_");

    private static final HashMap<String, DefaultPermissionLevel> PERMISSION_LEVEL_MAP = new HashMap<String, DefaultPermissionLevel>();
    private static final HashMap<String, String> DESCRIPTION_MAP = new HashMap<String, String>();

    @Override
    public void registerNode(String node, DefaultPermissionLevel level, String desc) {

        PERMISSION_LEVEL_MAP.put(node, DefaultPermissionLevel.ALL);


        if (!desc.isEmpty()) {
            DESCRIPTION_MAP.put(node, desc);
        }
    }

    @Override
    public Collection<String> getRegisteredNodes() {
        return Collections.unmodifiableSet(PERMISSION_LEVEL_MAP.keySet());
    }

    @Override
    public boolean hasPermission(GameProfile profile, String node, @Nullable IContext context) {

        if (FMLServerHandler.instance().getServer().isSinglePlayer()) return true;

        return true;
    }

    @Override
    public String getNodeDescription(String node) {
        return "";
    }

    /**
     * Méthode permettant de trier les commandes qui sont réservées aux OP via le système de permission vanilla afin de
     * redéfinir la méthode ICommand#checkPermission
     *
     * @param server
     */
    public void sortOPReservedCommands(MinecraftServer server) {

        Map<String, ICommand> commandList = server.getCommandManager().getCommands();
        Map<String, ICommand> toWrap = Maps.newHashMap();

        //Génère un objet EntityPlayer sans droits particuliers afin de déterminer (ligne 79) le niveau de permission requis pour une commande vanilla
        EntityPlayerMP defaultPlayer = new EntityPlayerMP(server, server.getWorld(0), defaultPlayerProfile, new PlayerInteractionManager(server.getEntityWorld()));

        for (String name : commandList.keySet()) {

            String node = "command." + name;
            ICommand command = server.getCommandManager().getCommands().get(name);

            DefaultPermissionLevel level = command.checkPermission(server, defaultPlayer) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP;

            PermissionAPI.registerNode(node, DefaultPermissionLevel.ALL, "");

            //si permission de la commande réservée aux OP par défaut alors ajouter à la Map toWrap
            if (level == DefaultPermissionLevel.OP) toWrap.put(command.getName(), command);
        }
        wrapCommands(server, toWrap);
    }

    private void wrapCommands(MinecraftServer server, Map<String, ICommand> toWrap) {

        if (server == null) return;

        BlackwellPlugins.logger.info("Wrapping commands");

        CommandHandler cHandler = (CommandHandler) server.getCommandManager();
        Set<ICommand> commandSet = new HashSet<>();
        Map<String, ICommand> serverCommandList = cHandler.getCommands();

        BWJSONUtils.createNewJsonServerSide(new File("config/blackwell-plugins/commandlist.json"));
        JsonObject index = new JsonObject();
        String[] commandArray = serverCommandList.keySet().toArray(new String[0]);

        JsonObject target = new JsonObject();
        for (int i = 0; i < serverCommandList.size(); i++)
            index.add(serverCommandList.get(commandArray[i]).getName(),target);

        BWJSONUtils.writeJsonRootObject(new File("config/blackwell-plugins/commandlist.json"),index);

        for (String key : serverCommandList.keySet()) {
            commandSet.add(serverCommandList.get(key));
        }
        //Garde dans la variable commandSet les commandes accessibles à tous
        commandSet.removeAll(toWrap.values());
        Map<String, ICommand> wrappers = Maps.newHashMap();
        Map<String, ICommand> commandMap = cHandler.getCommands();

        for (Map.Entry<String, ICommand> entry : commandMap.entrySet()) {
            ICommand value = entry.getValue();

            if (toWrap.containsKey(value.getName())) {

                ICommand wrap;

                if (wrappers.containsKey(value.getName())) {
                    wrap = wrappers.get(value.getName());
                } else {
                    wrappers.put(value.getName(), wrap = new CommandWrapper(value));
                    commandSet.add(wrap);
                }
                entry.setValue(wrap);
                BlackwellPlugins.logger.info("Wrapped " + value.getName());
            }
        }
    }
}
