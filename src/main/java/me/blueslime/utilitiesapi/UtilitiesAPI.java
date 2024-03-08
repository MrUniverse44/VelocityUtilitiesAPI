package me.blueslime.utilitiesapi;


import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import me.blueslime.utilitiesapi.utils.consumer.delay.ConsumerDelay;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

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
