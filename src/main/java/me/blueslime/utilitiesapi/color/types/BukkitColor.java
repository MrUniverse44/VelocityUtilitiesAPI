package me.blueslime.utilitiesapi.color.types;

import me.blueslime.utilitiesapi.color.ColorHandler;
import org.bukkit.ChatColor;

public class BukkitColor extends ColorHandler {
    @Override
    public String execute(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
