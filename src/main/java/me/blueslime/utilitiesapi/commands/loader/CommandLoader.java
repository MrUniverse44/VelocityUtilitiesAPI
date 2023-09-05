package me.blueslime.utilitiesapi.commands.loader;

import java.lang.reflect.Field;

import me.blueslime.utilitiesapi.commands.AdvancedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLoader {

    private static CommandLoader LOADER_INSTANCE = null;

    public static CommandLoader build(JavaPlugin plugin) {
        if (LOADER_INSTANCE == null) {
            LOADER_INSTANCE = new CommandLoader(plugin);
        }
        return LOADER_INSTANCE;
    }

    private CommandMap commandMap;

    private CommandLoader(JavaPlugin plugin) {
        Field bukkitCommandMap;

        try {
            bukkitCommandMap = plugin.getServer()
                    .getClass()
                    .getDeclaredField("commandMap");
        } catch (Exception ignored) {
            plugin.getLogger().info("Can't register plugin commands.");
            return;
        }

        bukkitCommandMap.setAccessible(true);

        CommandMap commandMap;

        try {
            commandMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());
            this.commandMap = commandMap;
        } catch (Exception ignored) {
            plugin.getLogger().info("Can't register plugin commands.");
        }
    }

    public CommandLoader register(AdvancedCommand<?> command) {
        return register(
                command.getCommand(),
                command
        );
    }

    public CommandLoader register(String commandName, BukkitCommand commandClass) {
        if (commandMap != null) {
            commandMap.register(commandName, commandClass);
        }
        return this;
    }

    public CommandLoader unregister(AdvancedCommand<?> command) {
        return unregister(command.getCommand());
    }

    public CommandLoader unregister(String commandName) {
        if (commandMap == null) {
            return this;
        }

        Command command = commandMap.getCommand(commandName);

        if (command == null) {
            return this;
        }

        command.unregister(
                commandMap
        );
        return this;
    }

    public void finish() {

    }

}