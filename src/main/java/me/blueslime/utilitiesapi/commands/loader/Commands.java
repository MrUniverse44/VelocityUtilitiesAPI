package me.blueslime.utilitiesapi.commands.loader;

import me.blueslime.utilitiesapi.commands.AdvancedCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Commands {

    private static Commands LOADER_INSTANCE = null;

    public static Commands build(JavaPlugin plugin) {
        if (LOADER_INSTANCE == null) {
            LOADER_INSTANCE = new CommandLoader(plugin);
        }
        return LOADER_INSTANCE;
    }

    public static void replaceCommandLoader(CommandLoader loader) {
        LOADER_INSTANCE = loader;
    }

    public abstract void registerCommand(final String name, AdvancedCommand<?> executable);

    public abstract void registerCommand(AdvancedCommand<?> executable, String alias);

    public abstract CommandLoader register(AdvancedCommand<?> command);

    public abstract CommandLoader register(String commandName, AdvancedCommand<?> commandClass);

    public CommandLoader unregister(AdvancedCommand<?> command) {
        return unregister(command.getCommand());
    }

    public abstract CommandLoader unregister(String commandName);

    public void finish() {

    }

}
