package me.blueslime.utilitiesapi.tools;

import org.bukkit.Color;

import java.util.Locale;

public class PluginTools {

    public static Color getColor(String argument) {
        switch (argument.toLowerCase(Locale.ENGLISH)) {
            case "black":
                return Color.BLACK;
            case "aqua":
                return Color.AQUA;
            case "blue":
                return Color.BLUE;
            case "fuchsia":
                return Color.FUCHSIA;
            case "gray":
                return Color.GRAY;
            case "green":
                return Color.GREEN;
            case "lime":
                return Color.LIME;
            case "maroon":
                return Color.MAROON;
            case "navy":
                return Color.NAVY;
            case "olive":
                return Color.OLIVE;
            case "orange":
                return Color.ORANGE;
            case "purple":
                return Color.PURPLE;
            case "red":
                return Color.RED;
            case "silver":
                return Color.SILVER;
            case "teal":
                return Color.TEAL;
            case "white":
                return Color.WHITE;
            case "yellow":
                return Color.YELLOW;
            default:
                String[] split = argument.replace(" ", "").split(",", 3);
                if (split.length >= 3) {
                    if (!isNumber(split[0]) || !isNumber(split[1]) || !isNumber(split[3])) {
                        return Color.WHITE;
                    }
                    return Color.fromRGB(
                            Integer.parseInt(split[0]),
                            Integer.parseInt(split[1]),
                            Integer.parseInt(split[3])
                    );
                } else if (split.length == 2) {
                    if (!isNumber(split[0]) || !isNumber(split[1])) {
                        return Color.WHITE;
                    }
                    return Color.fromRGB(
                            Integer.parseInt(split[0]),
                            Integer.parseInt(split[1]),
                            0
                    );
                } else {
                    if (!isNumber(split[0])) {
                        return Color.WHITE;
                    }
                    return Color.fromRGB(
                            Integer.parseInt(split[0]),
                            0,
                            0
                    );
                }
        }
    }

    public static boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }


}
