package me.blueslime.utilitiesapi.commands;

import me.blueslime.utilitiesapi.color.ColorHandler;
import me.blueslime.utilitiesapi.commands.loader.CommandLoader;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "NullableProblems"})
public abstract class AdvancedCommand<T extends JavaPlugin> extends BukkitCommand {
    private final T plugin;
    private String command;

    public AdvancedCommand(T plugin, String command) {
        this(plugin, command, "Plugin Command", "/" + command + " <args>", Collections.emptyList());
    }

    public AdvancedCommand(T plugin, String command, List<String> aliases) {
        this(plugin, command, "Plugin Command", "/" + command + " <args>", aliases);
    }

    public AdvancedCommand(T plugin, String command, String description, String usageMessage, List<String> aliases) {
        super(command, description, usageMessage, aliases);
        this.command = command;
        this.plugin = plugin;
    }

    public AdvancedCommand(T plugin) {
        this(plugin,null);
    }

    public void register() {
        if (this.command != null) {
            CommandLoader.build(plugin)
                    .register(this)
                    .finish();
        }
    }
    
    public void unregister() {
        CommandLoader.build(plugin)
                .unregister(this)
                .finish();
    }

    public AdvancedCommand<T> setCommand(String command) {
        this.command = command;
        super.setName(command);
        return this;
    }

    public AdvancedCommand<T> setAliases(List<String> aliases) {
        super.setAliases(aliases);
        return this;
    }

    public AdvancedCommand<T> setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    public AdvancedCommand<T> setUsage(String message) {
        super.setUsage(message);
        return this;
    }

    public abstract void executeCommand(Sender sender, String command, String[] arguments);

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        executeCommand(Sender.build(sender), this.command, args);
        return true;
    }

    public String getCommand() {
        return command;
    }

    public T getPlugin() {
        return plugin;
    }

    public static String colorize(String text) {
        return ColorHandler.convert(text);
    }
}


