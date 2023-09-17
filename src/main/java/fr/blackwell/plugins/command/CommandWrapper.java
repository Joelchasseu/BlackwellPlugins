package fr.blackwell.plugins.command;

import fr.blackwell.plugins.permission.BWPermissionManagement;
import fr.blackwell.plugins.permission.BWPlayer;
import fr.blackwell.plugins.permission.BWPlayerProfileManagement;
import fr.blackwell.plugins.utils.BWCommandUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CommandWrapper implements ICommand {

    final ICommand command;
    final String node;


    public CommandWrapper(ICommand command) {

        this.command = command;
        this.node = "command." + command.getName();
    }

    @Override
    public String getName() {
        return this.command.getName();
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return this.command.getUsage(sender);
    }

    @Override
    public List<String> getAliases() {
        return this.command.getAliases();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        this.command.execute(server, sender, args);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {

        return BWCommandUtils.checkPermission(server,sender,this.getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return this.command.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return this.command.isUsernameIndex(args, index);
    }

    @Override
    public int compareTo(ICommand o) {
        return this.command.compareTo(o);
    }
}
