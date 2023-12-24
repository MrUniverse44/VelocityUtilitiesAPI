package me.blueslime.utilitiesapi.tools;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderParser {
    public static String parse(Player player, String line) {
        return PlaceholderAPI.setPlaceholders(player, line);
    }
}
