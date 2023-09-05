package me.blueslime.utilitiesapi.commands;

import me.blueslime.utilitiesapi.color.ColorHandler;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused", "NullableProblems"})
public abstract class SimpleCommand<T extends JavaPlugin> implements CommandExecutor {
    private final T plugin;
    private String command;

    public SimpleCommand(T plugin, String command) {
        this.command = command;
        this.plugin = plugin;
    }

    public SimpleCommand(T plugin) {
        this(plugin,null);
    }

    public void register(JavaPlugin plugin) {
        if (this.command != null) {
            PluginCommand command = plugin.getCommand(this.command);

            if (command != null) {
                command.setExecutor(this);
            }
        }
    }

    public SimpleCommand<T> setCommand(String command) {
        this.command = command;
        return this;
    }

    public abstract void execute(Sender sender, String command, String[] arguments);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(Sender.build(sender), this.command, args);
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

