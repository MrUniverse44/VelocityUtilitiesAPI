package me.blueslime.velocity.utilitiesapi.utils;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import me.blueslime.velocity.utilitiesapi.color.ColorHandler;
import me.blueslime.velocity.utilitiesapi.configuration.VelocityConfiguration;
import me.blueslime.velocity.utilitiesapi.text.TextReplacer;
import me.blueslime.velocity.utilitiesapi.text.TextUtilities;
import net.kyori.adventure.text.Component;

import java.util.List;

@SuppressWarnings("unused")
public class Sender {

    private final CommandSource sender;

    private Sender(CommandSource sender) {
        this.sender = sender;
    }

    /**
     * Create a Sender instance
     * @param sender to be converted
     * @return Sender instance
     */
    public static Sender build(CommandSource sender) {
        return new Sender(sender);
    }

    /**
     * Check if the sender is a player
     * @return result
     */
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    /**
     * Convert the Sender to a player
     * @return Player
     */
    public Player toPlayer() {
        return (Player)sender;
    }

    /**
     * Check if the sender is a ConsoleCommandSender
     * @return boolean result
     */
    public boolean isConsole() {
        return sender instanceof ConsoleCommandSource;
    }

    /**
     * Convert the Sender to a ConsoleCommandSender
     * @return ConsoleCommandSender if the sender is a ConsoleCommandSender
     */
    public ConsoleCommandSource toConsole() {
        return (ConsoleCommandSource)sender;
    }

    /**
     * Check if a CommandSender contains a specified permission
     * @param permission to check
     * @return boolean result
     */
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    /**
     * Convert the sender to a Command Source
     * @return Entity
     */
    public CommandSource toSource() {
        return sender;
    }

    /**
     * Send a message to the sender
     * @param target player for PlaceholdersAPI if the server has it.
     * @param replacer replacements for the messages
     * @param messages to be sent
     */
    public void send(Player target, TextReplacer replacer, String... messages) {
        if (messages == null || messages.length == 0) {
            sender.sendMessage(Component.text(" "));
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

    /**
     * Send messages for the sender
     * @param replacer replacements for messages
     * @param messages to be sent
     */
    public void send(TextReplacer replacer, String... messages) {
        send(null, replacer, messages);
    }

    /**
     * Send messages to the sender
     * @param target player to cast PlaceholdersAPI if the server has it.
     * @param messages to be sent
     */
    public void send(Player target, String... messages) {
        send(target, null, messages);
    }

    /**
     * Send messages to the sender
     * @param messages to be sent
     */
    public void send(String... messages) {
        send((Player)null, null, messages);
    }

    /**
     * Send messages from a configuration path to the sender
     * @param target player to cast in Placeholders if the server has it.
     * @param configuration for search the current path
     * @param path of the message
     * @param def value if the path is not set
     * @param replacer replacements for messages
     */
    public void send(Player target, VelocityConfiguration configuration, String path, Object def, TextReplacer replacer) {
        Object ob = configuration.get(path, def);

        if (ob == null) {
            return;
        }

        if (ob instanceof List<?> list) {
            for (Object object : list) {
                send(
                    target,
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

    /**
     * Send messages from a configuration path to the sender
     * @param configuration for search the current path
     * @param path of the message
     * @param def value if the path is not set
     * @param replacer replacements for messages
     */
    public void send(VelocityConfiguration configuration, String path, Object def, TextReplacer replacer) {
        send(null, configuration, path, def, replacer);
    }

    /**
     * Send messages from a configuration path to the sender
     * @param configuration for search the current path
     * @param path of the message
     * @param def value if the path is not set
     */
    public void send(VelocityConfiguration configuration, String path, Object def) {
        send(null, configuration, path, def, null);
    }

    /**
     * Send messages from a configuration path to the sender
     * @param target player to cast in PlaceholdersAPI if the server has it.
     * @param configuration for search the current path
     * @param path of the message
     * @param def value if the path is not set
     */
    public void send(Player target, VelocityConfiguration configuration, String path, Object def) {
        send(target, configuration, path, def, null);
    }

    /**
     * Send base components to the sender
     * @param components to be sent
     */
    public void send(Component... components) {
        for (Component component : components) {
            sender.sendMessage(component);
        }
    }

    public void send(Component message) {
        sender.sendMessage(message);
    }

    /**
     * Send messages from a configuration path to the sender
     * @param target player to cast in Placeholders if the server has it.
     * @param configuration for search the current path
     * @param path of the message
     * @param replacer replacements for messages
     */
    public void send(Player target, VelocityConfiguration configuration, String path, TextReplacer replacer) {
        Object ob = configuration.get(path);

        if (ob == null) {
            return;
        }

        if (ob instanceof List<?> list) {
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

    /**
     * Send messages from a configuration path to the sender
     * @param configuration for search the current path
     * @param path of the message
     * @param replacer replacements for messages
     */
    public void send(VelocityConfiguration configuration, String path, TextReplacer replacer) {
        send(null, configuration, path, replacer);
    }

    /**
     * Send messages from a configuration path to the sender
     * @param target player to cast in PlaceholdersAPI if the server has it.
     * @param configuration for search the current path
     * @param path of the message
     */
    public void send(Player target, VelocityConfiguration configuration, String path) {
        send(target, configuration, path, null);
    }

    /**
     * Send messages from a configuration path to the sender
     * @param configuration for search the current path
     * @param path of the message
     */
    public void send(VelocityConfiguration configuration, String path) {
        send(configuration, path, null);
    }

    /**
     * Send a list of messages to the sender
     * @param messages to be sent
     * @param replacer replacements for messages
     */
    public void send(List<String> messages, TextReplacer replacer) {
        send(null, messages, replacer);
    }

    /**
     * Send a list of messages to the sender
     * @param target player to cast in PlaceholdersAPI if the server has it
     * @param messages to be sent
     * @param replacer replacements for messages
     */
    public void send(Player target, List<String> messages, TextReplacer replacer) {
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

    /**
     * Send a message list to the sender
     * @param messages to be sent
     */
    public void send(List<String> messages) {
        send(messages, null);
    }

    /**
     * Colorize a text value
     * @param text to be colorized
     * @return colored text
     */
    public static Component colorize(String text) {
        return ColorHandler.convert(text);
    }

    /**
     * Colorize a String List text value
     * @param list to be colorized
     * @return colored text
     */
    public static List<Component> colorizeList(List<String> list) {
        return TextUtilities.colorizeList(list);
    }
}