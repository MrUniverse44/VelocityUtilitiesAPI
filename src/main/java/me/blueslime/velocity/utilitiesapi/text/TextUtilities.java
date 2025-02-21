package me.blueslime.velocity.utilitiesapi.text;

import me.blueslime.velocity.utilitiesapi.color.ColorHandler;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class TextUtilities {
    /**
     * Colorize a {@link java.util.List<String>} adding hex or only legacy colors, checks if the server supports it.
     * @param stringList to be colorized.
     * @return colorized string list
     */
    public static List<String> colorizeListAsString(List<String> stringList) {
        stringList.replaceAll(TextUtilities::colorizeAsString);
        return stringList;
    }

    /**
     * Colorize a String adding hex or only legacy colors, checks if the server supports it.
     * @param message to be colorized.
     * @return colorized message
     */
    public static String colorizeAsString(String message) {
        return ColorHandler.convertAsString(message);
    }

    /**
     * Colorize a {@link java.util.List<String>} adding hex or only legacy colors, checks if the server supports it.
     * @param stringList to be colorized.
     * @return colorized string list
     */
    public static List<Component> colorizeList(List<String> stringList) {
        List<Component> finalList = new ArrayList<>();
        stringList.forEach(line -> finalList.add(colorize(line)));
        return finalList;
    }

    /**
     * Colorize a String adding hex or only legacy colors, checks if the server supports it.
     * @param message to be colorized.
     * @return colorized message
     */
    public static Component colorize(String message) {
        return ColorHandler.convert(message);
    }
}
