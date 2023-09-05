package me.blueslime.utilitiesapi.commands.sender;

import me.blueslime.utilitiesapi.color.ColorHandler;
import me.blueslime.utilitiesapi.text.TextReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unused")
public class Sender {
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

    public static String colorize(String text) {
        return ColorHandler.convert(text);
    }
}