package me.blueslime.velocity.utilitiesapi.utils;

public interface PluginReturnableConsumer<V, K> {
    V accept(K arg);
}
