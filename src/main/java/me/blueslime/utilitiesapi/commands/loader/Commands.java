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

    public static void replaceCommandLoader(Commands loader) {
        LOADER_INSTANCE = loader;
    }

    public abstract void registerCommand(final String name, AdvancedCommand<?> executable);

    public abstract void registerCommand(AdvancedCommand<?> executable, String alias);

    public abstract Commands register(AdvancedCommand<?> command);

    public abstract Commands register(String commandName, AdvancedCommand<?> commandClass);

    public Commands unregister(AdvancedCommand<?> command) {
        return unregister(command.getCommand());
    }

    public abstract Commands unregister(String commandName);

    public void finish() {

    }

}
