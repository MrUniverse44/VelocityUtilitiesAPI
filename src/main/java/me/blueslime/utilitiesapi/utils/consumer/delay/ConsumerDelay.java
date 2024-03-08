package me.blueslime.utilitiesapi.utils.consumer.delay;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class ConsumerDelay {
    private final JavaPlugin plugin;
    private final TimeUnit unit;
    private final long delay;

    private ConsumerDelay(JavaPlugin plugin, TimeUnit unit, int delay) {
        this.plugin = plugin;
        this.delay = delay;
        this.unit = unit;
    }

    /**
     * Value should be in seconds
     * @param plugin instance
     * @param unit to be converted
     * @param delay of this task.
     * @return created delay q
     */
    public static ConsumerDelay build(JavaPlugin plugin, TimeUnit unit, int delay) {
        return new ConsumerDelay(plugin, unit, delay);
    }

    /**
     * Value should be in seconds
     * @param plugin instance
     * @param unit to be converted
     * @param delay of this task.
     * @return created delay q
     */
    public static ConsumerDelay build(Class<? extends JavaPlugin> plugin, TimeUnit unit, int delay) {
        return new ConsumerDelay(JavaPlugin.getPlugin(plugin), unit, delay);
    }

    public long getRealDelay() {
        return calcDelay();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private int calcDelay() {
        return ((int)unit.toSeconds(delay)) * 20;
    }
}
