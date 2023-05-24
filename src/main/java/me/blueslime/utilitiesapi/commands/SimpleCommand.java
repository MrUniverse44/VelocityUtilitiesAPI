package me.blueslime.utilitiesapi.commands;

import me.blueslime.utilitiesapi.color.ColorHandler;
import me.blueslime.utilitiesapi.text.TextReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("unused")
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

    public static class Sender {
        private final CommandSender sender;

        private Sender(CommandSender sender) {
            this.sender = sender;
        }

        public static Sender build(CommandSender sender) {
            return new Sender(sender);
        }

        public boolean isPlayer() {
            return sender instanceof Player;
        }

        public Player toPlayer() {
            return (Player)sender;
        }

        public boolean isConsole() {
            return sender instanceof ConsoleCommandSender;
        }

        public ConsoleCommandSender toConsole() {
            return (ConsoleCommandSender)sender;
        }

        public boolean hasPermission(String permission) {
            return sender.hasPermission(permission);
        }

        public Entity toEntity() {
            return (Entity)sender;
        }

        public void send(TextReplacer replacer, String... messages) {
            if (messages == null || messages.length == 0) {
                sender.sendMessage(" ");
                return;
            }

            for (String message : messages) {
                sender.sendMessage(
                        colorize(
                                replacer == null ?
                                        message :
                                        replacer.apply(message)
                        )
                );
            }
        }

        public void send(String... messages) {
            send(null, messages);
        }

        public void send(ConfigurationSection configuration, String path, Object def, TextReplacer replacer) {
            Object ob = configuration.get(path, def);

            if (ob == null) {
                return;
            }

            if (ob instanceof List) {
                List<?> list = (List<?>)ob;
                for (Object object : list) {
                    send(
                            replacer,
                            object.toString()
                    );
                }
            } else {
                send(
                        colorize(
                                replacer == null ?
                                        ob.toString() :
                                        replacer.apply(ob.toString())
                        )
                );
            }
        }

        public void send(ConfigurationSection configuration, String path, Object def) {
            send(configuration, path, def, null);
        }

        public void send(BaseComponent... components) {
            if (isPlayer()) {
                toPlayer().spigot().sendMessage(components);
            }
        }

        public void send(ConfigurationSection configuration, String path, TextReplacer replacer) {
            Object ob = configuration.get(path);

            if (ob == null) {
                return;
            }

            if (ob instanceof List) {
                List<?> list = (List<?>)ob;
                for (Object object : list) {
                    send(
                            colorize(
                                    replacer == null ?
                                            object.toString() :
                                            replacer.apply(object.toString())
                            )
                    );
                }
            } else {
                send(
                        colorize(
                                replacer == null ?
                                        ob.toString() :
                                        replacer.apply(ob.toString())
                        )
                );
            }
        }

        public void send(ConfigurationSection configuration, String path) {
            send(configuration, path, null);
        }

        public void send(List<String> messages, TextReplacer replacer) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            for (String message : messages) {
                sender.sendMessage(
                        colorize(
                                replacer == null ?
                                        message :
                                        replacer.apply(message)
                        )
                );
            }
        }

        public void send(List<String> messages) {
            send(messages, null);
        }
    }

    public static String colorize(String text) {
        return ColorHandler.convert(text);
    }
}

