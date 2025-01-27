package me.blueslime.utilitiesapi.commands;

import me.blueslime.utilitiesapi.color.ColorHandler;
import me.blueslime.utilitiesapi.commands.loader.Commands;
import me.blueslime.utilitiesapi.commands.sender.Sender;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public abstract class AdvancedCommand<T extends JavaPlugin> extends BukkitCommand {
    private final List<String> aliases;
    protected final T plugin;
    private String command;

    public AdvancedCommand(T plugin, String command) {
        this(plugin, command, "Plugin Command", "/" + command + " <args>", Collections.emptyList());
    }

    public AdvancedCommand(T plugin, String command, List<String> aliases) {
        this(plugin, command, "Plugin Command", "/" + command + " <args>", aliases);
    }

    public AdvancedCommand(T plugin, FileConfiguration configuration, String commandPath, String aliasesPath) {
        this(
            plugin,
            configuration != null ? configuration.getString(commandPath, "") : "",
            "Plugin Command",
            "/<command> <args>",
            configuration != null ? configuration.getStringList(aliasesPath) : new ArrayList<>()
        );
    }

    public AdvancedCommand(T plugin, String command, String description, String usageMessage, List<String> aliases) {
        super(command, description, usageMessage, aliases);
        this.command = command;
        this.aliases = aliases;
        this.plugin = plugin;
    }

    public AdvancedCommand(T plugin) {
        this(plugin,null);
    }

    public boolean overwriteCommand() {
        return false;
    }

    public void register() {
        if (this.command != null) {
            Commands.build(plugin)
                .register(this)
                .finish();
        }
    }
    
    public void unregister() {
        Commands.build(plugin)
            .unregister(this)
            .finish();
    }

    public AdvancedCommand<T> setCommand(String command) {
        this.command = command;
        super.setName(command);
        return this;
    }

    public @NotNull AdvancedCommand<T> setAliases(@NotNull List<String> aliases) {
        super.setAliases(aliases);
        return this;
    }

    public @NotNull AdvancedCommand<T> setDescription(@NotNull String description) {
        super.setDescription(description);
        return this;
    }

    public @NotNull AdvancedCommand<T> setUsage(@NotNull String message) {
        super.setUsage(message);
        return this;
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Command Execution
     * @param sender Source object which is executing this command
     * @param command The alias of the command used
     * @param arguments All arguments passed to the command, split via ' '
     */
    public abstract void executeCommand(Sender sender, String command, String[] arguments);

    /**
     * Command execution
     * @param sender Source object which is executing this command
     * @param label The alias of the command used
     * @param arguments All arguments passed to the command, split via ' '
     * @return value
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        executeCommand(Sender.build(sender), this.command, arguments);
        return true;
    }

    /**
     * Tab Complete Execution
     * @param sender Source object which is executing this command
     * @param alias the alias being used
     * @param arguments All arguments passed to the command, split via ' '
     * @return result list
     * @throws IllegalArgumentException when the return value is null
     */
    public List<String> onTabComplete(Sender sender, String alias, String[] arguments) {
        return super.tabComplete(sender.toCommandSender(), alias, arguments);
    }

    /**
     * Tab Complete Execution
     * @param sender Source object which is executing this command
     * @param alias the alias being used
     * @param arguments All arguments passed to the command, split via ' '
     * @param location The position looked at by the sender, or null if none
     * @return result list
     * @throws IllegalArgumentException when the return value is null
     */
    public List<String> onTabComplete(Sender sender, String alias, String[] arguments, Location location) {
        return super.tabComplete(sender.toCommandSender(), alias, arguments);
    }

    /**
     * Tab Complete Execution
     * @param sender Source object which is executing this command
     * @param alias the alias being used
     * @param args All arguments passed to the command, split via ' '
     * @return result list
     * @throws IllegalArgumentException when the return value is null
     */
    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return onTabComplete(Sender.build(sender), alias, args);
    }

    /**
     * Tab Complete Execution
     * @param sender Source object which is executing this command
     * @param alias the alias being used
     * @param args All arguments passed to the command, split via ' '
     * @param location The position looked at by the sender, or null if none
     * @return result list
     * @throws IllegalArgumentException when the return value is null
     */
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return onTabComplete(Sender.build(sender), alias, args, location);
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


