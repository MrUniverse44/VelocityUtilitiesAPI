package me.blueslime.utilitiesapi.item.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PersistentDataNBT {
    private static PersistentDataNBT PERSISTENT_INSTANCE = null;
    private final Plugin plugin;

    public PersistentDataNBT(Plugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isAvailable() {
        return PERSISTENT_INSTANCE != null;
    }

    public static ItemStack setString(ItemStack itemStack, String key, String value) {
        if (!isAvailable()) {
            return itemStack;
        }
        return PERSISTENT_INSTANCE.set(itemStack, key, value);
    }

    public ItemStack set(@NotNull final ItemStack itemStack, final String key, final String value) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Removes a tag from an {@link ItemStack}.
     *
     * @param itemStack The current {@link ItemStack} to remove it.
     * @param key       The NBT key to remove.
     * @return An {@link ItemStack} that has the tag removed.
     */
    public ItemStack removeTag(ItemStack itemStack, String key) {
        if (isAvailable()) {
            return PERSISTENT_INSTANCE.remove(itemStack, key);
        }
        return itemStack;
    }

    public ItemStack remove(@NotNull final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets the NBT tag based on a given key.
     *
     * @param itemStack The {@link ItemStack} to get from.
     * @param key       The key to look for.
     * @return The tag that was stored in the {@link ItemStack}.
     */
    public static String getString(ItemStack itemStack, String key) {
        if (!isAvailable()) {
            return "";
        }
        return PERSISTENT_INSTANCE.get(itemStack, key);
    }

    public String get(@NotNull final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return "";
        }
        return meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    public static void initialize(Plugin plugin) {
        if (PERSISTENT_INSTANCE == null) {
            PERSISTENT_INSTANCE = new PersistentDataNBT(plugin);
        }
    }

}
