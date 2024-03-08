package me.blueslime.utilitiesapi.utils.consumer;

import me.blueslime.utilitiesapi.utils.consumer.annotated.DelayedAnnotation;
import me.blueslime.utilitiesapi.utils.consumer.delay.ConsumerDelay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public interface PluginConsumer<T> {

    T executeConsumer() throws Exception;

    interface PluginOutConsumer {
        void executeConsumer() throws Exception;
    }

    static  void process(PluginOutConsumer consumer) {
        try {
            consumer.executeConsumer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void process(String message, PluginOutConsumer consumer) {
        try {
            consumer.executeConsumer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static  void process(PluginOutConsumer consumer, Consumer<Exception> exception) {
        try {
            consumer.executeConsumer();
        } catch (Exception ex) {
            exception.accept(ex);
        }
    }

    @DelayedAnnotation
    static BukkitTask processDelayed(PluginOutConsumer consumer, Consumer<Exception> exception, ConsumerDelay delay) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    consumer.executeConsumer();
                } catch (Exception ex) {
                    exception.accept(ex);
                }
            }
        }.runTaskLater(
            delay.getPlugin(),
            delay.getRealDelay()
        );
    }

    @DelayedAnnotation
    static BukkitTask processDelayedAsynchronously(PluginOutConsumer consumer, Consumer<Exception> exception, ConsumerDelay delay) {
        return new BukkitRunnable() {
            public void run() {
                try {
                    consumer.executeConsumer();
                } catch (Exception ex) {
                    exception.accept(ex);
                }
            }
        }.runTaskLaterAsynchronously(
                delay.getPlugin(),
                delay.getRealDelay()
        );
    }

    static <T> T ofUnchecked(final PluginConsumer<T> template) {
        T results = null;
        try {
            results = template.executeConsumer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    static <T> T ofUnchecked(final PluginConsumer<T> template, final Consumer<Exception> exception, T defValue) {
        T results = defValue;
        try {
            results = template.executeConsumer();
        } catch (Exception ex) {
            exception.accept(ex);
        }
        return results;
    }

    static <T> T ofUnchecked(final PluginConsumer<T> template, final Consumer<Exception> exception) {
        T results = null;
        try {
            results = template.executeConsumer();
        } catch (Exception ex) {
            exception.accept(ex);
        }
        return results;
    }

    static <T> T ofUnchecked(final PluginConsumer<T> template, T defValue) {
        T results = defValue;
        try {
            results = template.executeConsumer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    static <T> T ofUnchecked(String message, final PluginConsumer<T> template) {
        T results = null;
        try {
            results = template.executeConsumer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    static <T> T ofUnchecked(String message, final PluginConsumer<T> template, T defValue) {
        T results = defValue;
        try {
            results = template.executeConsumer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }

    static <T> PluginConsumer<T> of(PluginConsumer<T> c){ return c; }
}



