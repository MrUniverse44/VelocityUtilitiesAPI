package me.blueslime.utilitiesapi.commands.loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import me.blueslime.utilitiesapi.commands.AdvancedCommand;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLoader extends Commands {

    private final Map<String, Command> bukkitCommands;
    private final CommandMap commandMap;

    @SuppressWarnings("unchecked")
    public CommandLoader(JavaPlugin plugin) {
        Field bukkitCommandMapField = PluginConsumer.ofUnchecked(
            () -> plugin.getServer().getClass().getDeclaredField("commandMap"),
            e -> {},
            () -> null
        );

        CommandMap commandMap = null;

        if (bukkitCommandMapField != null) {
            bukkitCommandMapField.setAccessible(true);

            commandMap = PluginConsumer.ofUnchecked(
                () -> (CommandMap)bukkitCommandMapField.get(plugin.getServer()),
                e -> {},
                () -> null
            );
        }

        if (commandMap == null) {
            // In this case CommandMap Field was not found, so we need to try with the method instead.
            commandMap = PluginConsumer.ofUnchecked(
                () -> {
                    Method getCommandMap = plugin.getServer().getClass().getDeclaredMethod("getCommandMap");
                    getCommandMap.setAccessible(true);
                    return (CommandMap)getCommandMap.invoke(plugin.getServer());
                },
                e -> plugin.getLogger().severe("Failed to get command map in this minecraft version."),
                () -> null
            );
        }

        this.commandMap = commandMap;
        this.bukkitCommands = PluginConsumer.ofUnchecked(
            () -> {
                Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
                bukkitCommands.setAccessible(true);
                return (Map<String, Command>) bukkitCommands.get(this.commandMap);
            },
            e -> {},
            () -> null
        );
    }

    @Override
    public void registerCommand(final String name, AdvancedCommand<?> executable) {
        final org.bukkit.command.Command oldCommand = commandMap.getCommand(name);

        if (
            oldCommand instanceof PluginIdentifiableCommand &&
            (
                (executable.overwriteCommand())
                || (!executable.overwriteCommand() && ((PluginIdentifiableCommand) oldCommand).getPlugin() == executable.getPlugin())
            )
        ) {
            bukkitCommands.remove(name);
            oldCommand.unregister(commandMap);
        }

        String fallbackName = executable.getPlugin().getName().toLowerCase(Locale.ENGLISH);

        commandMap.register(executable.getCommand(), fallbackName, executable);

        if (executable.getAliases() == null || executable.getAliases().isEmpty()) {
            return;
        }

        for (String alias : executable.getAliases()) {
            if (alias == null) {
                continue;
            }

            PluginConsumer.ofUnchecked(
                () -> {
                    if (commandMap.getCommand(alias) != null) {
                        bukkitCommands.remove(alias);
                    }
                    commandMap.register(alias, fallbackName, executable);
                    return true;
                },
                e -> executable.getPlugin().getLogger().warning("Failed to register command alias \"" + alias + "\". trying again with other method." + e.getMessage()),
                () -> {
                    registerCommand(executable, alias);
                    executable.getPlugin().getLogger().info("Registered command \"" + alias + "\".");
                    return true;
                }
            );
        }
    }

    @Override
    public void registerCommand(AdvancedCommand<?> executable, String alias) {
        PluginConsumer.process(
            () -> {
                final org.bukkit.command.Command oldCommand = commandMap.getCommand(alias);

                if (
                    oldCommand instanceof PluginIdentifiableCommand &&
                    (
                        (executable.overwriteCommand())
                        || (!executable.overwriteCommand() && ((PluginIdentifiableCommand) oldCommand).getPlugin() == executable.getPlugin())
                    )
                ) {
                    bukkitCommands.remove(alias);
                    oldCommand.unregister(commandMap);
                }

                String fallbackName = executable.getPlugin().getName().toLowerCase(Locale.ENGLISH);

                commandMap.register(alias, fallbackName, executable);
            },
            e -> {}
        );
    }

    @Override
    public CommandLoader register(AdvancedCommand<?> command) {
        return register(
            command.getCommand(),
            command
        );
    }

    @Override
    public CommandLoader register(String commandName, AdvancedCommand<?> commandClass) {
        if (commandMap != null) {
            registerCommand(commandName, commandClass);
        }
        return this;
    }

    @Override
    public CommandLoader unregister(AdvancedCommand<?> command) {
        return unregister(command.getCommand());
    }

    @Override
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

}