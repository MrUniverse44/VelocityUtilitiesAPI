package me.blueslime.utilitiesapi.text;

import me.blueslime.utilitiesapi.color.ColorHandler;

import java.util.List;

public class TextUtilities {
    /**
     * Colorize a {@link java.util.List<String>} adding hex or only legacy colors, checks if the server supports it.
     * @param stringList to be colorized.
     * @return colorized string list
     */
    public static List<String> colorizeList(List<String> stringList) {
        stringList.replaceAll(TextUtilities::colorize);
        return stringList;
    }

    /**
     * Colorize a String adding hex or only legacy colors, checks if the server supports it.
     * @param message to be colorized.
     * @return colorized message
     */
    public static String colorize(String message) {
        return ColorHandler.convert(message);
    }
}
