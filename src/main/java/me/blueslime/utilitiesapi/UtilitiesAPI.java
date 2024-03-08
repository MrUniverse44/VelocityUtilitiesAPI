package me.blueslime.utilitiesapi;

import org.bukkit.Bukkit;

public final class UtilitiesAPI {

    private static UtilitiesAPI INSTANCE = null;

    private final boolean placeholders;

    public static boolean hasPlaceholders() {
        if (INSTANCE == null) {
            INSTANCE = build();
        }
        return INSTANCE.hasPAPI();
    }

    private UtilitiesAPI() {
        placeholders = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static UtilitiesAPI build() {
        return new UtilitiesAPI();
    }

    public boolean hasPAPI() {
        return placeholders;
    }
}
